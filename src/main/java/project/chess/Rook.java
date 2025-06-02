package project.chess;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece
{
    public Rook(Colour colour) { super(colour); }

    @Override
    public char toFENSymbol()
    {
        return (colour == Colour.WHITE) ? 'R' : 'r';
    }

    @Override
    public String toAlgebraicNotation()
    {
        return "R";
    }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        return generateSlidingMoves(position, board, new int[]{-8, -1, 1, 8});
    }
}
