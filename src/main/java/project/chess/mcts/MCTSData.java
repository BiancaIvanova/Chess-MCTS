package project.chess.mcts;

import project.chess.model.Game;
import project.chess.pieces.Piece;

public class MCTSData
{
    private Game state;
    private String move;
    private int visits;
    private double wins;
    private Piece.Colour playerToMove;

    public MCTSData(Game state, String move, Piece.Colour playerToMove)
    {
        this.state = state;
        this.move = move;
        this.playerToMove = playerToMove;
        this.visits = 0;
        this.wins = 0.0;
    }

    public Game getState() { return state; }

    public String getMove() { return move; }

    public int getVisits() { return visits; }

    public double getWins() { return wins; }

    public Piece.Colour getPlayerToMove() { return playerToMove; }

    public void incrementVisits() { visits++; }

    public void addWin(double score) { wins += score; }

    @Override
    public String toString()
    {
        return move == null ? "(root)" : move + " [" + wins + "/" + visits + "]";
    }
}
