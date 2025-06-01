package project.chess;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece
{
    public Bishop(Colour colour) { super(colour); }

    @Override
    public char toFENSymbol()
    {
        return (colour == Colour.WHITE) ? 'B' : 'b';
    }

    @Override
    public String toAlgebraicNotation()
    {
        return "B";
    }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        return generateSlidingMoves(position, board, new int[]{-9, -7, 7, 9});
    }
}
