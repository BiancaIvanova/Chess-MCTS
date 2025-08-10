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
     *
     * HOWEVER this measures the whole JVM heap, so is not completely accurate. It would be better
     * to use a profiler to measure this instead of Java Runtime.
     */
    private void runMCTSWithMemoryUsage(Tree<MCTSData> tree, int simulations) {
        Runtime runtime = Runtime.getRuntime();

        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();

        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();
        mcts.runSimulations(tree, simulations);

        LinkedList<String> rankedMoves = mcts.getRankedMoves(tree);
        System.out.println("Ranked moves (best to worst):");
        int rank = 1;
        for (String move : rankedMoves.asIterable()) {
            System.out.printf("%d. %s\n", rank++, move);
        }

        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        long usedMemory = Math.max(0, afterMemory - beforeMemory);

        double usedMB = usedMemory / 1024.0 / 1024.0;
        System.out.printf("Memory used by MCTS: %.4f KB%n", usedMB);
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

    @Test
    void testSparseBoardRankings() {
        System.out.println("TEST: Sparse Board Rankings");
        String fen = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1";
        Tree<MCTSData> tree = buildTreeFromFEN(fen);
        runMCTSWithMemoryUsage(tree, 500);
    }

    @Test
    void testOtherBoardRankings() {
        System.out.println("TEST: Other Board Rankings");
        String fen = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w KQkq - 0 1";
        Tree<MCTSData> tree = buildTreeFromFEN(fen);
        runMCTSWithMemoryUsage(tree, 500);
    }

    @Test
    void testTrompTraxlerRankings() {
        System.out.println("TEST: Tromp Traxler Rankings");
        String fen = "n1n5/PPPk4/8/8/8/8/4Kppp/5N1N w - - 0 1";
        Tree<MCTSData> tree = buildTreeFromFEN(fen);
        runMCTSWithMemoryUsage(tree, 500);
    }

}
