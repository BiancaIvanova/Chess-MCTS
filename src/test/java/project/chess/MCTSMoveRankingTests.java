package project.chess;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import project.chess.datastructures.LinkedList;
import project.chess.datastructures.Tree;
import project.chess.datastructures.TreeNode;
import project.chess.mcts.MCTSData;
import project.chess.mcts.MonteCarloTreeSearch;

@SpringBootTest
public class MCTSMoveRankingTests {

    /**
     * Build an MCTS tree from a FEN position.
     */
    private Tree<MCTSData> buildTreeFromFEN(String fen) {
        Game game = new Game();
        game.importFEN(fen);

        // Root node: MCTSData with null move
        MCTSData rootData = new MCTSData(game, null, game.getCurrentTurn());

        Tree<MCTSData> tree = new Tree<>();
        tree.setRoot(rootData);  // correctly sets tree's root

        TreeNode<MCTSData> rootNode = tree.getRoot();
        if (rootNode == null) throw new IllegalStateException("Tree root is null!");

        // Populate children
        for (var movePair : game.getBoard().generateAllLegalMoveBoards(game.getCurrentTurn())) {
            Game childGame = new Game(game);
            childGame.makeMove(movePair);
            MCTSData childData = new MCTSData(childGame, movePair.getKey(), childGame.getCurrentTurn());
            rootNode.addChild(new TreeNode<>(childData));
        }

        return tree;
    }

    /**
     * Run MCTS and print moves ranked best-to-worst.
     */
    private void printRankedMoves(Tree<MCTSData> tree, int simulations) {
        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();
        mcts.runSimulations(tree, simulations);

        LinkedList<String> rankedMoves = mcts.getRankedMoves(tree);
        System.out.println("Ranked moves (best to worst):");
        int rank = 1;
        for (String move : rankedMoves.asIterable()) {
            System.out.printf("%d. %s\n", rank++, move);
        }
    }

    /**
     * Run MCTS on a tree, print ranked moves, and display memory usage for MCTS execution.
     */
    private void runMCTSWithMemoryUsage(Tree<MCTSData> tree, int simulations) {
        Runtime runtime = Runtime.getRuntime();

        // Memory before MCTS
        runtime.gc();
        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();

        // Run MCTS and print ranked moves
        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();
        mcts.runSimulations(tree, simulations);

        LinkedList<String> rankedMoves = mcts.getRankedMoves(tree);
        System.out.println("Ranked moves (best to worst):");
        int rank = 1;
        for (String move : rankedMoves.asIterable()) {
            System.out.printf("%d. %s\n", rank++, move);
        }

        // Memory after MCTS
        runtime.gc();
        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        long usedMemory = afterMemory - beforeMemory;

        double usedKB = usedMemory / 1024.0;
        System.out.printf("Memory used by MCTS: %.4f KB%n", usedKB);
    }


    @Test
    void testInitialPositionRankings() {
        System.out.println("TEST: Initial Position Rankings");
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Tree<MCTSData> tree = buildTreeFromFEN(fen);
        runMCTSWithMemoryUsage(tree, 500);
    }

    @Test
    void testKiwipeteRankings() {
        System.out.println("TEST: Kiwipete Position Rankings");
        String fen = "rnbqkb1r/pp1ppppp/5n2/2p5/2B5/5N2/PPPPPPPP/RNBQK2R w KQkq - 2 4";
        Tree<MCTSData> tree = buildTreeFromFEN(fen);
        runMCTSWithMemoryUsage(tree, 500);
    }
}
