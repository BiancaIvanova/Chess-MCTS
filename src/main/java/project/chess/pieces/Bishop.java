package project.chess.pieces;

import project.chess.Chessboard;
import project.chess.PieceType;

import java.util.List;

public class Bishop extends Piece
{
    public Bishop(Colour colour) { super(colour); }

    @Override
    public PieceType getType() { return PieceType.BISHOP; }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        return generateSlidingMoves(position, board, new int[]{-9, -7, 7, 9});
    }
}
