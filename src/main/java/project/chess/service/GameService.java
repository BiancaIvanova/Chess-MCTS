package project.chess.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import project.chess.controller.BoardStateDTO;
import project.chess.controller.MakeMoveRequest;
import project.chess.controller.StartGameRequest;
import project.chess.datastructure.LinkedList;
import project.chess.datastructure.Tree;
import project.chess.mcts.MCTSData;
import project.chess.mcts.MCTSTreeGenerator;
import project.chess.mcts.MonteCarloTreeSearch;
import project.chess.model.Game;
import project.chess.persistence.entity.EvaluationEntity;
import project.chess.persistence.entity.EvaluationRangeEntity;
import project.chess.persistence.entity.GameEntity;
import project.chess.persistence.entity.MoveEntity;
import project.chess.persistence.repository.EvaluationRangeRepository;
import project.chess.persistence.repository.EvaluationRepository;
import project.chess.persistence.repository.GameRepository;
import project.chess.persistence.repository.MoveRepository;
import project.chess.piece.Piece;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class GameService
{
    private static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private static final int MCTS_TREE_DEPTH = 1;
    private static final int MCTS_SIMULATIONS = 100;

    private final GameRepository gameRepository;
    private final MoveRepository moveRepository;
    private final EvaluationRepository evaluationRepository;
    private final EvaluationRangeRepository evaluationRangeRepository;

    public GameService(GameRepository gameRepository,
                       MoveRepository moveRepository,
                       EvaluationRepository evaluationRepository,
                       EvaluationRangeRepository evaluationRangeRepository)
    {
        this.gameRepository = gameRepository;
        this.moveRepository = moveRepository;
        this.evaluationRepository = evaluationRepository;
        this.evaluationRangeRepository = evaluationRangeRepository;
    }

    @Transactional
    public BoardStateDTO startGame(StartGameRequest request, HttpSession session)
    {
        Game game = new Game();

        String initialFEN = request != null && request.getInitialFEN() != null && !request.getInitialFEN().isBlank()
                ? request.getInitialFEN()
                : STARTING_FEN;

        game.importFEN(initialFEN);

        GameEntity gameEntity = new GameEntity();
        gameEntity.setStartedAt(LocalDateTime.now(ZoneOffset.UTC));
        gameEntity.setEndedAt(null);
        gameEntity.setResult("ONGOING");
        gameEntity.setGameMode(request != null && request.getGameMode() != null ? request.getGameMode() : "Human vs Engine");
        gameEntity.setUserColour(request != null && request.getUserColour() != null ? request.getUserColour() : "White");
        gameEntity.setCompleted(false);
        gameEntity.setTotalUserTimeMs(0);
        gameEntity.setAverageMoveQuality(null);
        gameEntity.setAverageMoveTimeMs(null);

        Integer gameId = gameRepository.save(gameEntity);

        session.setAttribute("game", game);
        session.setAttribute("gameId", gameId);
        session.setAttribute("lastMoveStartedAt", System.currentTimeMillis());

        return new BoardStateDTO(game, gameId);
    }

    public BoardStateDTO getBoardState(HttpSession session)
    {
        Game game = (Game) session.getAttribute("game");
        Integer gameId = (Integer) session.getAttribute("gameId");

        if (game == null)
        {
            StartGameRequest request = new StartGameRequest();
            request.setGameMode("Human vs Engine");
            request.setUserColour("White");
            return startGame(request, session);
        }

        return new BoardStateDTO(game, gameId);
    }

    @Transactional
    public BoardStateDTO makeMove(MakeMoveRequest request, HttpSession session)
    {
        Game game = (Game) session.getAttribute("game");

        Integer gameId = request != null && request.getGameId() != null
                ? request.getGameId()
                : (Integer) session.getAttribute("gameId");

        if (game == null)
        {
            startGame(null, session);
            game = (Game) session.getAttribute("game");
            gameId = (Integer) session.getAttribute("gameId");
        }

        if (request == null || request.getMove() == null || request.getMove().isBlank())
        {
            throw new IllegalArgumentException("Move cannot be empty.");
        }

        if (gameId == null || !gameRepository.existsById(gameId))
        {
            throw new IllegalArgumentException("Game does not exist.");
        }

        String moveSan = request.getMove();
        String fenBefore = game.getFEN();
        Piece.Colour sideToMove = game.getCurrentTurn();
        int moveNumber = game.getMoveHistory().size() + 1;
        int timeTakenMs = calculateMoveTime(request, session);

        boolean validMove = game.makeValidMove(moveSan);

        if (!validMove)
        {
            throw new IllegalArgumentException("Illegal move: " + moveSan);
        }

        String fenAfter = game.getFEN();

        MoveEntity moveEntity = new MoveEntity();
        moveEntity.setGameId(gameId);
        moveEntity.setMoveNumber(moveNumber);
        moveEntity.setSideToMove(sideToMove.toString());
        moveEntity.setMoveSan(moveSan);
        moveEntity.setTimeTakenMs(timeTakenMs);
        moveEntity.setFenBefore(fenBefore);
        moveEntity.setFenAfter(fenAfter);
        moveEntity.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));

        Integer moveId = moveRepository.save(moveEntity);

        updateGameSummary(gameId, game);

        session.setAttribute("game", game);
        session.setAttribute("gameId", gameId);
        session.setAttribute("lastMoveStartedAt", System.currentTimeMillis());

        String fenForEvaluation = fenBefore;

        Integer finalGameId = gameId;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
        {
            @Override
            public void afterCommit()
            {
                runEvaluationInBackground(
                        fenForEvaluation,
                        moveSan,
                        sideToMove,
                        moveId,
                        finalGameId
                );
            }
        });

        return new BoardStateDTO(game, gameId, moveId);
    }

    @Transactional
    public void deleteGame(Integer gameId)
    {
        gameRepository.deleteById(gameId);
    }

    private int calculateMoveTime(MakeMoveRequest request, HttpSession session)
    {
        if (request.getTimeTakenMs() != null)
        {
            return request.getTimeTakenMs();
        }

        Long lastMoveStartedAt = (Long) session.getAttribute("lastMoveStartedAt");

        if (lastMoveStartedAt == null)
        {
            return 0;
        }

        return (int) Math.max(0, System.currentTimeMillis() - lastMoveStartedAt);
    }

    public EvaluationEntity getEvaluationForMove(Integer moveId)
    {
        return evaluationRepository.findByMoveId(moveId);
    }

    private void runEvaluationInBackground(String fenBefore,
                                           String moveSan,
                                           Piece.Colour sideToMove,
                                           Integer moveId,
                                           Integer gameId)
    {
        Thread evaluationThread = new Thread(() -> {
            try
            {
                System.out.println("Starting evaluation for move_id = " + moveId);
                System.out.println("Move being evaluated = " + moveSan);
                System.out.println("Side to move = " + sideToMove);
                System.out.println("FEN used for evaluation = " + fenBefore);

                Game evaluationGame = new Game();
                evaluationGame.importFEN(fenBefore);

                long evaluationStartTime = System.currentTimeMillis();

                MCTSEvaluation mctsEvaluation = evaluateMoveWithMCTS(evaluationGame, moveSan, sideToMove);

                int evaluationTimeMs = (int) (System.currentTimeMillis() - evaluationStartTime);

                System.out.println("MCTS finished for move_id = " + moveId);
                System.out.println("Evaluation score = " + mctsEvaluation.getScore());
                System.out.println("Best move = " + mctsEvaluation.getBestMoveSan());

                EvaluationRangeEntity range = findEvaluationRange(mctsEvaluation.getScore());

                EvaluationEntity evaluationEntity = new EvaluationEntity();
                evaluationEntity.setMoveId(moveId);
                evaluationEntity.setRangeId(range.getRangeId());
                evaluationEntity.setEvaluationScore(mctsEvaluation.getScore());
                evaluationEntity.setBestMoveSan(mctsEvaluation.getBestMoveSan());
                evaluationEntity.setEvaluationTimeMs(evaluationTimeMs);
                evaluationEntity.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));

                evaluationRepository.save(evaluationEntity);

                System.out.println("Evaluation saved for move_id = " + moveId);
            }
            catch (Exception exception)
            {
                System.out.println("Evaluation failed for move_id = " + moveId);
                exception.printStackTrace();
            }
        });

        evaluationThread.start();
    }

    private MCTSEvaluation evaluateMoveWithMCTS(Game game, String moveSan, Piece.Colour sideToMove)
    {
        System.out.println("Generating MCTS tree...");

        Tree<MCTSData> tree = MCTSTreeGenerator.generateTree(new Game(game), MCTS_TREE_DEPTH, sideToMove);

        System.out.println("Tree generated.");
        System.out.println("Running MCTS simulations...");

        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();
        mcts.runSimulations(tree, MCTS_SIMULATIONS);

        System.out.println("Simulations finished.");

        LinkedList<String> rankedMoves = mcts.getRankedMoves(tree);

        System.out.println("Ranked move count = " + rankedMoves.size());
        System.out.println("Move searched for = " + moveSan);

        if (rankedMoves.isEmpty())
        {
            return new MCTSEvaluation(BigDecimal.ZERO, null);
        }

        String bestMoveSan = rankedMoves.get(0);
        int moveRank = -1;

        for (int i = 0; i < rankedMoves.size(); i++)
        {
            if (rankedMoves.get(i).equals(moveSan))
            {
                moveRank = i;
                break;
            }
        }

        if (moveRank == -1)
        {
            return new MCTSEvaluation(BigDecimal.ZERO, bestMoveSan);
        }

        double score;

        if (rankedMoves.size() == 1)
        {
            score = 10.0;
        }
        else
        {
            score = 10.0 * (rankedMoves.size() - 1 - moveRank) / (rankedMoves.size() - 1);
        }

        return new MCTSEvaluation(
                BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP),
                bestMoveSan
        );
    }

    private EvaluationRangeEntity findEvaluationRange(BigDecimal evaluationScore)
    {
        createDefaultEvaluationRangesIfMissing();

        EvaluationRangeEntity range = evaluationRangeRepository.findRangeForScore(evaluationScore);

        if (range == null)
        {
            throw new IllegalStateException("No evaluation range exists for score " + evaluationScore);
        }

        return range;
    }

    private void createDefaultEvaluationRangesIfMissing()
    {
        if (evaluationRangeRepository.count() > 0)
        {
            return;
        }

        createEvaluationRange("0.00", "2.00", "Blunder", "A very weak move that significantly worsens the position.");
        createEvaluationRange("2.01", "4.00", "Mistake", "A weak move where a stronger alternative was available.");
        createEvaluationRange("4.01", "6.00", "Inaccuracy", "A reasonable move, but not one of the strongest available options.");
        createEvaluationRange("6.01", "8.00", "Good", "A strong move that maintains or improves the position.");
        createEvaluationRange("8.01", "10.00", "Excellent", "One of the strongest available moves in the current position.");
    }

    private void createEvaluationRange(String minScore, String maxScore, String label, String description)
    {
        EvaluationRangeEntity range = new EvaluationRangeEntity();

        range.setMinScore(new BigDecimal(minScore));
        range.setMaxScore(new BigDecimal(maxScore));
        range.setEvalName(label);
        range.setDescription(description);

        evaluationRangeRepository.save(range);
    }

    private void updateGameSummary(Integer gameId, Game game)
    {
        List<MoveEntity> moves = moveRepository.findByGameIdOrderByMoveNumberAsc(gameId);

        int totalMoveTime = 0;

        for (MoveEntity move : moves)
        {
            totalMoveTime += move.getTimeTakenMs();
        }

        Double averageEvaluation = evaluationRepository.getAverageEvaluationScore(gameId);

        GameEntity gameEntity = new GameEntity();
        gameEntity.setGameId(gameId);
        gameEntity.setTotalUserTimeMs(totalMoveTime);
        gameEntity.setAverageMoveTimeMs(moves.isEmpty() ? null : totalMoveTime / moves.size());
        gameEntity.setAverageMoveQuality(averageEvaluation == null
                ? null
                : BigDecimal.valueOf(averageEvaluation).setScale(2, RoundingMode.HALF_UP));
        gameEntity.setResult(game.getResult().toString());
        gameEntity.setCompleted(game.isGameOver());

        if (game.isGameOver())
        {
            gameEntity.setEndedAt(LocalDateTime.now(ZoneOffset.UTC));
        }

        gameRepository.updateSummary(gameEntity);
    }

    private static class MCTSEvaluation
    {
        private final BigDecimal score;
        private final String bestMoveSan;

        public MCTSEvaluation(BigDecimal score, String bestMoveSan)
        {
            this.score = score;
            this.bestMoveSan = bestMoveSan;
        }

        public BigDecimal getScore()
        {
            return score;
        }

        public String getBestMoveSan()
        {
            return bestMoveSan;
        }
    }
}