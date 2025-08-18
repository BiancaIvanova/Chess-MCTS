package project.chess.piece;

import project.chess.model.Chessboard;
import project.chess.model.PieceType;

import java.util.List;

public class Rook extends Piece
{
    private static final int[] ROOK_DIRECTIONS = {-8, -1, 1, 8};

    public Rook(Colour colour) { super(colour); }
    public Rook(Rook other) { super(other); }

    @Override
    public PieceType getType() { return PieceType.ROOK; }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        return generateSlidingMoves(position, board, ROOK_DIRECTIONS);
    }
}
