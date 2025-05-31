package project.chess;

import java.util.List;

public abstract class Piece
{
    public enum Colour
    {
        WHITE,
        BLACK;
    }

    protected final Colour colour;

    public Piece(Colour colour) { this.colour = colour; }

    public Colour getColour() { return this.colour; }

    public abstract char getNotationSymbol();

    public abstract List<Integer> generateMoves(int position, Chessboard board);

    @Override
    public String toString() {return this.getNotationSymbol() + ""; }
}
