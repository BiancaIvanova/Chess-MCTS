package project.chess.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import project.chess.controller.AnalysisEvaluationDTO;
import project.chess.controller.BoardStateDTO;
import project.chess.controller.FenRequest;
import project.chess.controller.MakeMoveRequest;
import project.chess.datastructure.LinkedList;
import project.chess.datastructure.Tree;
import project.chess.mcts.MCTSData;
import project.chess.mcts.MCTSTreeGenerator;
import project.chess.mcts.MonteCarloTreeSearch;
import project.chess.model.Game;
import project.chess.piece.Piece;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalysisService
{
    private static final String STARTING_FEN =
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private static final int MCTS_TREE_DEPTH = 1;
    private static final int MCTS_SIMULATIONS = 100;

    public BoardStateDTO getAnalysisState(HttpSession session)
    {
        Game analysisGame = getOrCreateAnalysisGame(session);

        return new BoardStateDTO(analysisGame);
    }

    public BoardStateDTO importFEN(FenRequest request,
                                   HttpSession session)
    {
        if (request == null
                || request.getFen() == null
                || request.getFen().isBlank())
        {
            throw new IllegalArgumentException("Invalid FEN string entered");
        }

        Game analysisGame = new Game();

        try
        {
            analysisGame.importFEN(request.getFen());
        }
        catch (Exception exception)
        {
            throw new IllegalArgumentException("Invalid FEN string entered");
        }

        session.setAttribute("analysisGame", analysisGame);
        session.setAttribute("analysisEvaluation", null);

        return new BoardStateDTO(analysisGame);
    }

    public BoardStateDTO makeAnalysisMove(MakeMoveRequest request,
                                          HttpSession session)
    {
        Game analysisGame = getOrCreateAnalysisGame(session);

        if (request == null
                || request.getMove() == null
                || request.getMove().isBlank())
        {
            throw new IllegalArgumentException("Move cannot be empty.");
        }

        boolean validMove = analysisGame.makeValidMoveNoEnforceTurn(request.getMove());

        if (!validMove)
        {
            throw new IllegalArgumentException("Illegal move: " + request.getMove());
        }

        session.setAttribute("analysisGame", analysisGame);
        session.setAttribute("analysisEvaluation", null);

        return new BoardStateDTO(analysisGame);
    }

    public BoardStateDTO resetAnalysisBoard(HttpSession session)
    {
        Game analysisGame = new Game();
        analysisGame.importFEN(STARTING_FEN);

        session.setAttribute("analysisGame", analysisGame);
        session.setAttribute("analysisEvaluation", null);

        return new BoardStateDTO(analysisGame);
    }

    public void evaluatePositionAsync(String colour,
                                      HttpSession session)
    {
        Game analysisGame = getOrCreateAnalysisGame(session);

        String fen = analysisGame.getFEN();

        session.setAttribute("analysisEvaluation", null);

        Thread evaluationThread = new Thread(() -> {
            try
            {
                Piece.Colour colourToAnalyse = parseColour(colour);

                Game evaluationGame = new Game();
                evaluationGame.importFEN(fen);

                Tree<MCTSData> tree = MCTSTreeGenerator.generateTree(
                        new Game(evaluationGame),
                        MCTS_TREE_DEPTH,
                        colourToAnalyse
                );

                MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();

                mcts.runSimulations(tree, MCTS_SIMULATIONS);

                LinkedList<String> rankedMoves =
                        mcts.getRankedMoves(tree);

                List<String> rankedMoveList = new ArrayList<>();

                for (int i = 0; i < rankedMoves.size(); i++)
                {
                    rankedMoveList.add(rankedMoves.get(i));
                }

                AnalysisEvaluationDTO evaluation =
                        new AnalysisEvaluationDTO(
                                colourToAnalyse.toString(),
                                fen,
                                rankedMoveList
                        );

                session.setAttribute("analysisEvaluation", evaluation);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        });

        evaluationThread.start();
    }

    public AnalysisEvaluationDTO getEvaluation(HttpSession session)
    {
        return (AnalysisEvaluationDTO)
                session.getAttribute("analysisEvaluation");
    }

    private Game getOrCreateAnalysisGame(HttpSession session)
    {
        Game analysisGame =
                (Game) session.getAttribute("analysisGame");

        if (analysisGame == null)
        {
            analysisGame = new Game();
            analysisGame.importFEN(STARTING_FEN);

            session.setAttribute("analysisGame", analysisGame);
        }

        return analysisGame;
    }

    private Piece.Colour parseColour(String colour)
    {
        if (colour == null)
        {
            throw new IllegalArgumentException("Colour must be provided.");
        }

        if (colour.equalsIgnoreCase("WHITE"))
        {
            return Piece.Colour.WHITE;
        }

        if (colour.equalsIgnoreCase("BLACK"))
        {
            return Piece.Colour.BLACK;
        }

        throw new IllegalArgumentException(
                "Colour must be WHITE or BLACK."
        );
    }
}