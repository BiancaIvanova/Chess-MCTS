package project.chess.pieces;

import project.chess.model.Chessboard;
import project.chess.model.PieceType;

import java.util.List;

public class Queen extends Piece
{
    private static final int[] QUEEN_DIRECTIONS = {-9, -8, -7, -1, 1, 7, 8, 9};

    public Queen(Piece.Colour colour) { super(colour); }
    public Queen(Queen other) { super(other); }

    @Override
    public PieceType getType() { return PieceType.QUEEN; }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        return generateSlidingMoves(position, board, QUEEN_DIRECTIONS);
    }
}
