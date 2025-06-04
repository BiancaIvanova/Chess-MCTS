package project.chess.pieces;
import project.chess.Chessboard;
import project.chess.PieceType;

import java.util.ArrayList;
import java.util.List;

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

        int row = position / 8;
        int col = position % 8;

        for (int offset : offsets)
        {
            int target = position + offset;

            if (target < 0 || target >= 64) continue;

            int targetRow = target / 8;
            int targetCol = target % 8;
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
