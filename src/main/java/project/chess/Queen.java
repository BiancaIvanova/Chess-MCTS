package project.chess;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece
{
    public Queen(Piece.Colour colour) { super(colour); }

    @Override
    public char toFENSymbol()
    {
        return (colour == Colour.WHITE) ? 'Q' : 'q';
    }

    @Override
    public String toAlgebraicNotation()
    {
        return "Q";
    }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        return generateSlidingMoves(position, board, new int[]{-9, -8, -7, -1, 1, 7, 8, 9});
    }
}
