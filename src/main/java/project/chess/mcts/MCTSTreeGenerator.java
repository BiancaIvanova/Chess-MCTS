package project.chess.mcts;

import project.chess.datastructure.Pair;
import project.chess.datastructure.Tree;
import project.chess.datastructure.TreeNode;
import project.chess.model.Chessboard;
import project.chess.model.Game;
import project.chess.piece.Piece;

import java.util.List;

public class MCTSTreeGenerator
{
    public static Tree<MCTSData> generateTree(Game rootGame, int depth)
    {
        return generateTree(rootGame, depth, rootGame.getCurrentTurn());
    }

    public static Tree<MCTSData> generateTree(Game rootGame, int depth, Piece.Colour rootPlayer)
    {
        Tree<MCTSData> tree = new Tree<>();

        MCTSData rootData = new MCTSData(rootGame, null, rootPlayer);
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
        Piece.Colour colourToMove = parentGame.getCurrentTurn();

        List<Pair<String, Chessboard>> legalMoves = parentGame.getBoard().generateAllLegalMoveBoards(colourToMove);

        for (Pair<String, Chessboard> move : legalMoves)
        {
            String moveSAN = move.getKey();
            Game newGame = new Game(parentGame);

            newGame.makeMove(move);

            MCTSData childData = new MCTSData(newGame, moveSAN, newGame.getCurrentTurn());
            TreeNode<MCTSData> childNode = new TreeNode<>(childData);

            node.addChild(childNode);

            expandNodeRecursive(childNode, maxDepth, currentDepth + 1);
        }
    }
}