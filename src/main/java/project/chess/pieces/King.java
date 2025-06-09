package project.chess.pieces;

import project.chess.BoardUtils;
import project.chess.CastlingRight;
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

        // Castling logic
        if (this.getColour() == Colour.WHITE && position == BoardUtils.toIndex(0, 4))
        {
            // Kingside (white): squares 5, 6 must be empty, castling right must exist, and those squares must not be under attack
            if (board.castlingRights.contains(CastlingRight.WHITE_KINGSIDE)
                    && board.getPiece(BoardUtils.toIndex(0, 5)) == null
                    && board.getPiece(BoardUtils.toIndex(0, 6)) == null
                    && !board.isSquareAttacked(BoardUtils.toIndex(0, 4), Colour.BLACK)
                    && !board.isSquareAttacked(BoardUtils.toIndex(0, 5), Colour.BLACK)
                    && !board.isSquareAttacked(BoardUtils.toIndex(0, 6), Colour.BLACK))
            {
                moves.add(BoardUtils.toIndex(0, 6)); // O-O
            }

            // Queenside (white): squares 1, 2, 3 must be empty and not attacked
            if (board.castlingRights.contains(CastlingRight.WHITE_QUEENSIDE)
                    && board.getPiece(BoardUtils.toIndex(0, 1)) == null
                    && board.getPiece(BoardUtils.toIndex(0, 2)) == null
                    && board.getPiece(BoardUtils.toIndex(0, 3)) == null
                    && !board.isSquareAttacked(BoardUtils.toIndex(0, 4), Colour.BLACK)
                    && !board.isSquareAttacked(BoardUtils.toIndex(0, 3), Colour.BLACK)
                    && !board.isSquareAttacked(BoardUtils.toIndex(0, 2), Colour.BLACK))
            {
                moves.add(BoardUtils.toIndex(0, 2)); // O-O-O
            }
        }
        else if (this.getColour() == Colour.BLACK && position == BoardUtils.toIndex(7, 4))
        {
            if (board.castlingRights.contains(CastlingRight.BLACK_KINGSIDE)
                    && board.getPiece(BoardUtils.toIndex(7, 5)) == null
                    && board.getPiece(BoardUtils.toIndex(7, 6)) == null
                    && !board.isSquareAttacked(BoardUtils.toIndex(7, 4), Colour.WHITE)
                    && !board.isSquareAttacked(BoardUtils.toIndex(7, 5), Colour.WHITE)
                    && !board.isSquareAttacked(BoardUtils.toIndex(7, 6), Colour.WHITE))
            {
                moves.add(BoardUtils.toIndex(7, 6)); // O-O
            }

            if (board.castlingRights.contains(CastlingRight.BLACK_QUEENSIDE)
                    && board.getPiece(BoardUtils.toIndex(7, 1)) == null
                    && board.getPiece(BoardUtils.toIndex(7, 2)) == null
                    && board.getPiece(BoardUtils.toIndex(7, 3)) == null
                    && !board.isSquareAttacked(BoardUtils.toIndex(7, 4), Colour.WHITE)
                    && !board.isSquareAttacked(BoardUtils.toIndex(7, 3), Colour.WHITE)
                    && !board.isSquareAttacked(BoardUtils.toIndex(7, 2), Colour.WHITE))
            {
                moves.add(BoardUtils.toIndex(7, 2)); // O-O-O
            }
        }

        return moves;
    }
}
