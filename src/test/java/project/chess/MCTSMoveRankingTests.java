package project.chess;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import project.chess.datastructures.LinkedList;
import project.chess.datastructures.Tree;
import project.chess.datastructures.TreeNode;
import project.chess.mcts.MCTSData;
import project.chess.mcts.MonteCarloTreeSearch;
import project.chess.pieces.Piece;

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

        TreeNode<MCTSData> rootNode = tree.getRoot(); // <-- you'll need a getter in Tree
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

    @Test
    void testInitialPositionRankings() {
        System.out.println("TEST: Initial Position Rankings");
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Tree<MCTSData> tree = buildTreeFromFEN(fen);
        printRankedMoves(tree, 500);
    }

    @Test
    void testKiwipeteRankings() {
        System.out.println("TEST: Kiwipete Position Rankings");
        String fen = "rnbqkb1r/pp1ppppp/5n2/2p5/2B5/5N2/PPPPPPPP/RNBQK2R w KQkq - 2 4";
        Tree<MCTSData> tree = buildTreeFromFEN(fen);
        printRankedMoves(tree, 500);
    }
}
