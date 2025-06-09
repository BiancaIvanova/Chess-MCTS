package project.chess.pieces;

import project.chess.BoardUtils;
import project.chess.CastlingRight;
import project.chess.Chessboard;
import project.chess.PieceType;

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

        // Add castling moves
        moves.addAll(generateCastlingMoves(position, board));

        return moves;
    }

    private List<Integer> generateCastlingMoves(int position, Chessboard board)
    {
        List<Integer> castlingMoves = new ArrayList<>();

        if (this.colour == Colour.WHITE && position == BoardUtils.toIndex(0, 4))
        {
            int f1 = BoardUtils.toIndex(0, 5);
            int g1 = BoardUtils.toIndex(0, 6);
            int b1 = BoardUtils.toIndex(0, 1);
            int c1 = BoardUtils.toIndex(0, 2);
            int d1 = BoardUtils.toIndex(0, 3);

            int[] emptySquaresWhiteKingside = new int[]{f1, g1};
            int[] safeSquaresWhiteKingside = new int[]{f1, g1};

            if (board.castlingRights.contains(CastlingRight.WHITE_KINGSIDE) &&
                    canCastle(board, emptySquaresWhiteKingside, safeSquaresWhiteKingside))
            {
                castlingMoves.add(g1); // O-O
            }

            int[] emptySquaresWhiteQueenside = new int[]{b1, c1, d1};
            int[] safeSquaresWhiteQueenside = new int[]{d1, c1};

            if (board.castlingRights.contains(CastlingRight.WHITE_QUEENSIDE) &&
                    canCastle(board, emptySquaresWhiteQueenside, safeSquaresWhiteQueenside))
            {
                castlingMoves.add(c1); // O-O-O
            }
        }
        else if (this.colour == Colour.BLACK && position == BoardUtils.toIndex(7, 4))
        {
            int f8 = BoardUtils.toIndex(7, 5);
            int g8 = BoardUtils.toIndex(7, 6);
            int b8 = BoardUtils.toIndex(7, 1);
            int c8 = BoardUtils.toIndex(7, 2);
            int d8 = BoardUtils.toIndex(7, 3);

            int[] emptySquaresBlackKingside = new int[]{f8, g8};
            int[] safeSquaresBlackKingside = new int[]{f8, g8};

            if (board.castlingRights.contains(CastlingRight.BLACK_KINGSIDE) &&
                    canCastle(board, emptySquaresBlackKingside, safeSquaresBlackKingside))
            {
                castlingMoves.add(g8); // O-O
            }

            int[] emptySquaresBlackQueenside = new int[]{b8, c8, d8};
            int[] safeSquaresBlackQueenside = new int[]{d8, c8};

            if (board.castlingRights.contains(CastlingRight.BLACK_QUEENSIDE) &&
                    canCastle(board, emptySquaresBlackQueenside, safeSquaresBlackQueenside))
            {
                castlingMoves.add(c8); // O-O-O
            }
        }

        return castlingMoves;
    }

    private boolean canCastle(Chessboard board, int[] emptySquares, int[] safeSquares)
    {
        for (int square : emptySquares)
        {
            if (board.getPiece(square) != null) return false;
        }
        for (int square : safeSquares)
        {
            if (wouldBeInCheck(board, board.getKingPosition(colour), square)) return false;
        }

        return true;
    }

    private boolean wouldBeInCheck(Chessboard board, int from, int to)
    {
        Chessboard testBoard = new Chessboard();
        testBoard.importFEN(board.toFEN());
        testBoard.move(from, to);
        return testBoard.isInCheck(this.colour);
    }
}
