package project.chess.pieces;

import project.chess.Chessboard;
import project.chess.PieceType;

import java.util.List;

public class Queen extends Piece
{
    public Queen(Piece.Colour colour) { super(colour); }

    @Override
    public PieceType getType() { return PieceType.QUEEN; }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        return generateSlidingMoves(position, board, new int[]{-9, -8, -7, -1, 1, 7, 8, 9});
    }
}
