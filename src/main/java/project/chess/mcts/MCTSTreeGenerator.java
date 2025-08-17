package project.chess.mcts;

import project.chess.model.Chessboard;
import project.chess.model.Game;
import project.chess.datastructures.TreeNode;
import project.chess.datastructures.Pair;
import project.chess.datastructures.Tree;
import project.chess.pieces.Piece;

import java.util.List;

public class MCTSTreeGenerator
{
    public static Tree<MCTSData> generateTree(Game rootGame, int depth)
    {
        Tree<MCTSData> tree = new Tree<>();

        MCTSData rootData = new MCTSData(rootGame, null, Piece.Colour.WHITE);
        tree.setRoot(rootData);

        expandNodeRecursive(tree.getRoot(), depth, 0);

        return tree;
    }

    private static void expandNodeRecursive(TreeNode<MCTSData> node, int maxDepth, int currentDepth)
    {
        if (currentDepth >= maxDepth)
            return;

        MCTSData data = node.getValue();
        Game parentGame = data.getState();
        Piece.Colour colourToMove = data.getPlayerToMove();

        List<Pair<String, Chessboard>> legalMoves = parentGame.getBoard().generateAllLegalMoveBoards(colourToMove);

        for (Pair<String, Chessboard> move : legalMoves)
        {
            String moveSAN = move.getKey();
            Game newGame = cloneGame(parentGame);

            newGame.makeMove(move);

            Piece.Colour nextPlayer = (colourToMove == Piece.Colour.WHITE) ? Piece.Colour.BLACK : Piece.Colour.WHITE;
            MCTSData childData = new MCTSData(newGame, moveSAN, nextPlayer);

            TreeNode<MCTSData> childNode = new TreeNode<>(childData);
            node.addChild(childNode); // attaches to the correct node in the tree

            expandNodeRecursive(childNode, maxDepth, currentDepth + 1);
        }
    }

    private static Game cloneGame(Game game)
    {
        return new Game(game);
    }
}
