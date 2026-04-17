package project.chess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import project.chess.datastructure.LinkedList;
import project.chess.datastructure.Tree;
import project.chess.mcts.MCTSData;
import project.chess.mcts.MCTSTreeGenerator;
import project.chess.mcts.MonteCarloTreeSearch;
import project.chess.model.Game;
import project.chess.piece.Piece;

import java.math.BigDecimal;
import java.math.RoundingMode;

@SpringBootApplication
public class ChessApplication
{
    private static final int MCTS_TREE_DEPTH = 1;
    private static final int MCTS_SIMULATIONS = 100;

    public static void main(String[] args)
    {
        SpringApplication.run(ChessApplication.class, args);

//        Game game = new Game();
//        game.importFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
//
//        String moveSan = "e4";
//        Piece.Colour sideToMove = game.getCurrentTurn();
//
//        System.out.println("Testing MCTS evaluation");
//        System.out.println("FEN = " + game.getFEN());
//        System.out.println("Move being evaluated = " + moveSan);
//        System.out.println("Side to move = " + sideToMove);
//
//        long startTime = System.currentTimeMillis();
//
//        MCTSEvaluation evaluation = evaluateMoveWithMCTS(game, moveSan, sideToMove);
//
//        long endTime = System.currentTimeMillis();
//
//        System.out.println("Evaluation complete.");
//        System.out.println("Time taken = " + (endTime - startTime) + "ms");
//        System.out.println("Score = " + evaluation.getScore());
//        System.out.println("Best move = " + evaluation.getBestMoveSan());
    }

    private static MCTSEvaluation evaluateMoveWithMCTS(Game game, String moveSan, Piece.Colour sideToMove)
    {
        System.out.println("Generating MCTS tree...");

        Tree<MCTSData> tree = MCTSTreeGenerator.generateTree(new Game(game), MCTS_TREE_DEPTH, sideToMove);

        System.out.println("Tree generated.");

        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();

        System.out.println("Running MCTS simulations...");

        mcts.runSimulations(tree, MCTS_SIMULATIONS);

        System.out.println("Simulations finished.");

        LinkedList<String> rankedMoves = mcts.getRankedMoves(tree);

        System.out.println("Ranked move count = " + rankedMoves.size());
        System.out.println("Ranked moves:");

        for (int i = 0; i < rankedMoves.size(); i++)
        {
            System.out.println(i + ": " + rankedMoves.get(i));
        }

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

        System.out.println("Move rank = " + moveRank);

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