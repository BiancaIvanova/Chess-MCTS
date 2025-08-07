package project.chess.mcts;

import project.chess.Chessboard;
import project.chess.Game;
import project.chess.datastructures.TreeNode;
import project.chess.datastructures.Pair;
import project.chess.datastructures.Tree;
import project.chess.pieces.Piece;

import java.util.List;
import java.util.Random;

public class MonteCarloTreeSearch
{
    private static final double EXPLORATION_PARAMETER = Math.sqrt(2);

    public String findBestMove(Tree<MCTSData> tree, int simulations)
    {
        TreeNode<MCTSData> root = tree.getRoot();

        for (int i = 0; i < simulations; i++)
        {
            TreeNode<MCTSData> selectedNode = select(root);
            TreeNode<MCTSData> expandedNode = expand(selectedNode);
            double result = simulate(expandedNode);
            backpropagate(expandedNode, result);
        }

        // Choose best child (highest average win rate)
        TreeNode<MCTSData> bestChild = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (TreeNode<MCTSData> child : root.getChildren())
        {
            double winRate = (child.getValue().getVisits() > 0)
                    ? child.getValue().getWins() / child.getValue().getVisits()
                    : 0;

            if (winRate > bestValue)
            {
                bestValue = winRate;
                bestChild = child;
            }
        }

        return (bestChild != null ) ? bestChild.getValue().getMove() : null;
    }

    /**
     * 1. Selection: Select the next node using UCB1
     */
    private TreeNode<MCTSData> select(TreeNode<MCTSData> node)
    {
        while (!node.getChildren().isEmpty())
        {
            node = bestUCTChild(node);
        }
        return node;
    }

    private TreeNode<MCTSData> bestUCTChild(TreeNode<MCTSData> node)
    {
        double bestValue = Double.NEGATIVE_INFINITY;
        TreeNode<MCTSData> selectedNode = null;

        for (TreeNode<MCTSData> child : node.getChildren())
        {
            double uctValue = uctValue(child, node);
            if (uctValue > bestValue)
            {
                bestValue = uctValue;
                selectedNode = child;
            }
        }
        return (selectedNode != null) ? selectedNode : node.getChildren().getFirst();
    }

    private double uctValue(TreeNode<MCTSData> child, TreeNode<MCTSData> parent)
    {
        // UCT formula:
        int childVisits = child.getValue().getVisits();
        if (childVisits == 0) return Double.MAX_VALUE;

        double winRate = child.getValue().getWins() / childVisits;
        double explore = Math.sqrt(Math.log(parent.getValue().getVisits() + 1) / childVisits);

        return winRate + EXPLORATION_PARAMETER * explore;
    }

    /**
     * 2. Expansion
     */
    private TreeNode<MCTSData> expand(TreeNode<MCTSData> node)
    {
        // If at a leaf node, the algorithm is already at max expansion
        if (node.getChildren().isEmpty()) return node;

        // Pick one unvisited child (or just random if they all have been visited)
        TreeNode<MCTSData> bestNode = null;
        for (TreeNode<MCTSData> child : node.getChildren())
        {
            if (child.getValue().getVisits() == 0) return child;
        }

        // If all children have been visited, pick a random node
        List<TreeNode<MCTSData>> children = node.getChildren();
        return children.get(new Random().nextInt(children.size()));
    }

    /**
     * 3. Simulation (random playout from this node)
     */
    private double simulate(TreeNode<MCTSData> node)
    {
        Game game = new Game(node.getValue().getState());
        Piece.Colour playerColour = node.getValue().getPlayerToMove();

        // Random playout to the end
        // TODO fix so that it's not random playout, but less computationally intensive
        int maxPlayoutDepth = 30;

        for (int d = 0; d < maxPlayoutDepth && !game.isGameOver(); d++)
        {
            List<Pair<String, Chessboard>> legalMoves = game.getBoard().generateAllLegalMoveBoards(playerColour);

            if (legalMoves.isEmpty()) break;

            Pair<String, Chessboard> move = legalMoves.get(new Random().nextInt(legalMoves.size()));
            game.makeMove(move);

            playerColour = (playerColour == Piece.Colour.WHITE) ? Piece.Colour.BLACK : Piece.Colour.WHITE;
        }

        // Output score: 1 if our player won, 0 for loss, 0.5 for draw
        Piece.Colour winner = game.getWinner();
        return (winner == node.getValue().getPlayerToMove()) ? 1 : 0;
    }

    /**
     * 4. Backpropagation
     */
    private void backpropagate(TreeNode<MCTSData> node, double result)
    {
        while (node != null)
        {
            node.getValue().incrementVisits();
            node.getValue().addWin(result);
            node = node.getParent();
        }
    }
}
