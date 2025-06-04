package project.chess.pieces;

import project.chess.Chessboard;
import project.chess.PieceType;

import java.util.ArrayList;
import java.util.List;

/*
- Pawns move differently based on colour
- Pawn can move one square forward if it's empty
- Pawn can move two squares forward if it's on its starting rank and both squares are empty
- Pawn can capture diagonally one square forward-left or forward-right if there is an opponent piece
- Additionally, en passant and promotion exists
 */

public class Pawn extends Piece
{
    public Pawn(Colour colour) { super(colour); }

    @Override
    public PieceType getType() { return PieceType.PAWN; }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        List<Integer> moves = new ArrayList<>();

        addForwardMoves(position, board, moves);
        addCaptureMoves(position, board, moves);

        return moves;
    }

    private void addForwardMoves(int position, Chessboard board, List<Integer> moves)
    {
        int direction = (colour == Colour.WHITE) ? 8 : -8;
        int startRow = (colour == Colour.WHITE) ? 1 : 6;
        int row = position / BOARD_WIDTH;

        int oneStep = position + direction;
        if (isOnBoard(oneStep) && board.getPiece(oneStep) == null)
        {
            moves.add(oneStep);

            int twoStep = position + (direction * 2);
            if (row == startRow && isOnBoard(twoStep) && board.getPiece(twoStep) == null)
            {
                moves.add(twoStep);
            }
        }
    }

    private void addCaptureMoves(int position, Chessboard board, List<Integer> moves)
    {
        int direction = (this.getColour() == Colour.WHITE) ? 8 : -8;
        int col = position % BOARD_WIDTH;
        int[] captureOffsets = {direction - 1, direction + 1};

        for (int offset : captureOffsets)
        {
            int target = position + offset;
            if (isOnBoard(target))
            {
                int targetCol = target % BOARD_WIDTH;

                if (Math.abs(targetCol - col) == 1)
                {
                    Piece targetPiece = board.getPiece(target);

                    if (targetPiece != null && targetPiece.getColour() != this.getColour())
                    {
                        moves.add(target);
                    }
                }
            }
        }
    }
}
