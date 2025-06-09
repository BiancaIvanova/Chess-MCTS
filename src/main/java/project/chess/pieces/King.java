package project.chess.pieces;

import project.chess.BoardUtils;
import project.chess.Chessboard;
import project.chess.PieceType;

import static project.chess.Chessboard.BOARD_WIDTH;

import java.util.ArrayList;
import java.util.List;


public class King extends Piece
{
    private static final int[] KING_DIRECTIONS = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(Colour colour) { super(colour); }

    @Override
    public PieceType getType() { return PieceType.KING; }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        List<Integer> moves = new ArrayList<>();

        int prevCol = BoardUtils.getFile(position);

        for (int dir : KING_DIRECTIONS)
        {
            int target = position + dir;

            if (!isOnBoard(target)) continue;

            int currentCol = BoardUtils.getFile(target);
            if (isWrapping(prevCol, currentCol, dir)) continue;

            Piece targetPiece = board.getPiece(target);

            if (targetPiece == null || targetPiece.getColour() != this.getColour())
            {
                moves.add(target);
            }
        }

        return moves;
    }
}
