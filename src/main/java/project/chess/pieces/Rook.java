package project.chess.pieces;

import project.chess.Chessboard;
import project.chess.PieceType;

import java.util.List;

public class Rook extends Piece
{
    public Rook(Colour colour) { super(colour); }

    @Override
    public PieceType getType() { return PieceType.ROOK; }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        return generateSlidingMoves(position, board, new int[]{-8, -1, 1, 8});
    }
}
