package project.chess.piece;

import project.chess.model.Chessboard;
import project.chess.model.PieceType;

import java.util.List;

public class Bishop extends Piece
{
    private static final int[] BISHOP_DIRECTIONS = {-9, -7, 7, 9};

    public Bishop(Colour colour) { super(colour); }
    public Bishop(Bishop other) { super(other); }

    @Override
    public PieceType getType() { return PieceType.BISHOP; }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        return generateSlidingMoves(position, board, BISHOP_DIRECTIONS);
    }
}
