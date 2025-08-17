package project.chess.mcts;

import project.chess.model.Chessboard;
import project.chess.model.Game;
import project.chess.datastructures.TreeNode;
import project.chess.datastructures.Pair;
import project.chess.datastructures.Tree;
import project.chess.datastructures.LinkedList;
import project.chess.pieces.Piece;

import java.util.List;
import java.util.Random;

public class MonteCarloTreeSearch
{
    private static final double EXPLORATION_PARAMETER = Math.sqrt(2);
    private static final int MAX_PLAYOUT_DEPTH = 15;
    private static final double ROLLOUT_TEMPERATURE = 1.0;

    private final Random random = new Random();

    public void runSimulations(Tree<MCTSData> tree, int simulations)
    {
        TreeNode<MCTSData> root = tree.getRoot();

        for (int i = 0; i < simulations; i++)
        {
            TreeNode<MCTSData> selectedNode = select(root);
            TreeNode<MCTSData> expandedNode = expand(selectedNode);
            double result = simulate(expandedNode);
            backpropagate(expandedNode, result);
        }
    }

    public LinkedList<String> getRankedMoves(Tree<MCTSData> tree)
    {
        TreeNode<MCTSData> root = tree.getRoot();
        LinkedList<TreeNode<MCTSData>> children = root.getChildren();
        LinkedList<TreeNode<MCTSData>> sorted = new LinkedList<>();

        for (TreeNode<MCTSData> child : children.asIterable())
        {
            double winRateChild = child.getValue().getVisits() > 0
                    ? child.getValue().getWins() / child.getValue().getVisits()
                    : 0;

            int index = 0;
            boolean inserted = false;

            for (TreeNode<MCTSData> s : sorted.asIterable())
            {
                double winRateS = s.getValue().getVisits() > 0
                        ? s.getValue().getWins() / s.getValue().getVisits()
                        : 0;

                if (winRateChild > winRateS)
                {
                    sorted.insert(child, index);
                    inserted = true;
                    break;
                }
                index++;
            }

            if (!inserted) sorted.append(child);
        }

        LinkedList<String> rankedMoves = new LinkedList<>();
        for (TreeNode<MCTSData> node : sorted.asIterable())
        {
            rankedMoves.append(node.getValue().getMove());
        }

        return rankedMoves;
    }

    public String findBestMove(Tree<MCTSData> tree, int simulations)
    {
        runSimulations(tree, simulations);

        LinkedList<String> rankedMoves = getRankedMoves(tree);

        return rankedMoves.isEmpty() ? null : rankedMoves.get(0);
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

        for (TreeNode<MCTSData> child : node.getChildren().asIterable())
        {
            double uctValue = uctValue(child, node);
            if (uctValue > bestValue)
            {
                bestValue = uctValue;
                selectedNode = child;
            }
        }
        return (selectedNode != null) ? selectedNode : node.getChildren().get(0);
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
        for (TreeNode<MCTSData> child : node.getChildren().asIterable())
        {
            if (child.getValue().getVisits() == 0) return child;
        }

        // If all children have been visited, pick a random node
        LinkedList<TreeNode<MCTSData>> children = node.getChildren();
        return children.get(new Random().nextInt(children.size()));
    }

    /**
     * 3. Simulation (pseudorandom playout from this node)
     */
    private double simulate(TreeNode<MCTSData> node)
    {
        Game game = new Game(node.getValue().getState());
        Piece.Colour playerColour = node.getValue().getPlayerToMove();

        HeuristicEvaluator evaluator = new HeuristicEvaluator();
        double r = random.nextDouble();

        // Random playout to the end
        // TODO fix so that it's not random playout, but less computationally intensive

        for (int d = 0; d < MAX_PLAYOUT_DEPTH && !game.isGameOver(); d++)
        {
            List<Pair<String, Chessboard>> legalMoves = game.getBoard().generateAllLegalMoveBoards(playerColour);
            if (legalMoves.isEmpty()) break;

            Pair<String, Chessboard> chosenMove = selectMovePseudorandomly(legalMoves, evaluator, playerColour, random);

            game.makeMove(chosenMove);

            // Switch player colour
            playerColour = (playerColour == Piece.Colour.WHITE) ? Piece.Colour.BLACK : Piece.Colour.WHITE;
        }

        // Output score: 1 if our player won, 0 for loss, 0.5 for draw
        // If game doesn't end, evaluate stronger position
        Piece.Colour winner = game.getWinner();

        if (game.isGameOver())
        {
            if (winner == null) return 0.5; // true draw
            return (winner == node.getValue().getPlayerToMove()) ? 1 : 0;
        }
        else
        {
            // Max depth reached, game not finished: use heuristic
            double score = evaluator.evaluate(game.getBoard(), node.getValue().getPlayerToMove());
            return 0.5 + 0.5 * Math.tanh(score / 10); // hyperbolic tangent function
        }
    }

    private Pair<String, Chessboard> selectMovePseudorandomly(List<Pair<String, Chessboard>> legalMoves, HeuristicEvaluator evaluator, Piece.Colour playerColour, Random random)
    {
        double[] weights = new double[legalMoves.size()];
        double sumWeights = 0;

        // Compute softmax probabilities
        for (int i = 0; i < legalMoves.size(); i++)
        {
            double score = evaluator.evaluate(legalMoves.get(i).getValue(), playerColour);
            weights[i] = Math.exp(score / ROLLOUT_TEMPERATURE);
            sumWeights += weights[i];
        }

        for (int i = 0; i < weights.length; i++) weights[i] /= sumWeights;

        // Roulette-wheel selection
        double r = random.nextDouble();
        double cumulative = 0;

        for (int i = 0; i < weights.length; i++)
        {
            cumulative += weights[i];
            if (r < cumulative) return legalMoves.get(i);
        }

        return legalMoves.getLast();
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
