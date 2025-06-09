package project.chess.pieces;
import project.chess.BoardUtils;
import project.chess.Chessboard;
import project.chess.PieceType;

import java.util.ArrayList;
import java.util.List;

import static project.chess.Chessboard.BOARD_SIZE;

public class Knight extends Piece
{
    public Knight(Colour colour) { super(colour); }

    @Override
    public PieceType getType() { return PieceType.KNIGHT; }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        List<Integer> moves = new ArrayList<>();
        int[] offsets = {-17, -15, -10, -6, 6, 10, 15, 17};

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
