package project.chess.pieces;
import project.chess.model.BoardUtils;
import project.chess.model.Chessboard;
import project.chess.model.PieceType;

import java.util.ArrayList;
import java.util.List;

import static project.chess.model.Chessboard.BOARD_SIZE;

public class Knight extends Piece
{
    private static final int[] KNIGHT_DIRECTIONS = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(Colour colour) { super(colour); }
    public Knight(Knight other) { super(other); }

    @Override
    public PieceType getType() { return PieceType.KNIGHT; }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        List<Integer> moves = new ArrayList<>();
        int[] offsets = KNIGHT_DIRECTIONS;

        int row = BoardUtils.getRank(position);
        int col = BoardUtils.getFile(position);

        for (int offset : offsets)
        {
            int target = position + offset;

            if (target < 0 || target >= BOARD_SIZE) continue;

            int targetRow = BoardUtils.getRank(target);
            int targetCol = BoardUtils.getFile(target);
            int rowDelta = Math.abs(targetRow - row);
            int colDelta = Math.abs(targetCol - col);

            if (!((rowDelta == 2 && colDelta == 1) || (rowDelta == 1 && colDelta == 2))) continue;

            Piece targetSquarePiece = board.getPiece(target);

            if (targetSquarePiece == null || targetSquarePiece.getColour() != this.getColour())
            {
                moves.add(target);
            }
        }

        return moves;
    }
}
