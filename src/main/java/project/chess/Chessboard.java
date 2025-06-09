package project.chess;

import project.chess.datastructures.*;
import project.chess.pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.EnumSet;

public class Chessboard
{
    public static final int BOARD_WIDTH = 8;
    public static final int BOARD_SIZE = 64;

    // King position cache
    private int whiteKingPosition = -1;
    private int blackKingPosition = -1;

    private IHashDynamic<Integer, Piece> boardMap;

    public final EnumSet<CastlingRight> castlingRights;

    public Chessboard()
    {
        boardMap = new HashingDynamic<>();
        castlingRights = EnumSet.allOf(CastlingRight.class);
    }

    public Piece getPiece(int position)
    {
        if (boardMap.contains(position))
        {
            return boardMap.item(position);
        }
        return null;
    }

    public void setPiece(int position, Piece piece)
    {
        Piece oldPiece = getPiece(position);

        if (piece == null)
        {
            if (oldPiece != null && oldPiece.getType() == PieceType.KING)
            {
                if (oldPiece.getColour() == Piece.Colour.WHITE) whiteKingPosition = -1;
                else blackKingPosition = -1;
            }

            boardMap.delete(position);
        }
        else
        {
            boardMap.add(position, piece);

            // Update King position cache
            if (piece.getType() == PieceType.KING)
            {
                if (piece.getColour() == Piece.Colour.WHITE) whiteKingPosition = position;
                else blackKingPosition = position;
            }
        }
    }

    public void move(int from, int to)
    {
        Piece movingPiece = getPiece(from);
        if ( movingPiece == null ) return;

        if (movingPiece.getType() == PieceType.KING)
        {
            if (movingPiece.getColour() == Piece.Colour.WHITE)
                whiteKingPosition = to;
            else
                blackKingPosition = to;
        }

        if (!castlingRights.isEmpty())
        {
            updateCastlingRights(from, to);
        }

        // Move the piece
        setPiece(to, movingPiece);
        setPiece(from, null);

        // Handle castling rook movement
        if ((movingPiece.getType() == PieceType.KING) && (Math.abs(to - from) == 2))
        {
            int rookFrom, rookTo;

            if (to > from)
            {
                // Kingside castling
                rookFrom = BoardUtils.toIndex(BoardUtils.getRank(from), 7);
                rookTo = BoardUtils.toIndex(BoardUtils.getRank(from), 5);
            }
            else
            {
                // Queenside castling
                rookFrom = BoardUtils.toIndex(BoardUtils.getRank(from), 0);
                rookTo = BoardUtils.toIndex(BoardUtils.getRank(from), 3);
            }

            Piece rook = getPiece(rookFrom);
            setPiece(rookTo, rook);
            setPiece(rookFrom, null);
        }

        // TODO en passant, promotion, and halfmove clock
    }

    private void updateCastlingRights(int from, int to)
    {
        Piece movingPiece = getPiece(from);

        // Remove castling rights if necessary
        if (movingPiece.getType() == PieceType.KING)
        {
            if (movingPiece.getColour() == Piece.Colour.WHITE)
            {
                castlingRights.remove(CastlingRight.WHITE_KINGSIDE);
                castlingRights.remove(CastlingRight.WHITE_QUEENSIDE);
            }
            else
            {
                castlingRights.remove(CastlingRight.BLACK_KINGSIDE);
                castlingRights.remove(CastlingRight.BLACK_QUEENSIDE);
            }
        }

        if (movingPiece.getType() == PieceType.ROOK)
        {
            // If rook moves from original position, revoke appropriate castling right
            if (from == BoardUtils.toIndex(0, 0)) // a1
                castlingRights.remove(CastlingRight.WHITE_QUEENSIDE);
            else if (from == BoardUtils.toIndex(0, 7)) // h1
                castlingRights.remove(CastlingRight.WHITE_KINGSIDE);
            else if (from == BoardUtils.toIndex(7, 0)) // a8
                castlingRights.remove(CastlingRight.BLACK_QUEENSIDE);
            else if (from == BoardUtils.toIndex(7, 7)) // h8
                castlingRights.remove(CastlingRight.BLACK_KINGSIDE);
        }

        // Remove castling rights if a rook is captured
        Piece capturedPiece = getPiece(to);
        if (capturedPiece != null && capturedPiece.getType() == PieceType.ROOK)
        {
            if (to == BoardUtils.toIndex(0, 0)) // a1
                castlingRights.remove(CastlingRight.WHITE_QUEENSIDE);
            else if (to == BoardUtils.toIndex(0, 7)) // h1
                castlingRights.remove(CastlingRight.WHITE_KINGSIDE);
            else if (to == BoardUtils.toIndex(7, 0)) // a8
                castlingRights.remove(CastlingRight.BLACK_QUEENSIDE);
            else if (to == BoardUtils.toIndex(7, 7)) // h8
                castlingRights.remove(CastlingRight.BLACK_KINGSIDE);
        }
    }

    public boolean isOccupied(int position)
    {
        return boardMap.contains(position);
    }

    public String toFEN()
    {
        StringBuilder fen = new StringBuilder();

        // Loop from row 7 to 0
        for (int row = ( BOARD_WIDTH - 1 ); row >= 0; row--)
        {
            int emptyCount = 0;

            for (int col = 0; col < BOARD_WIDTH; col++)
            {
                int position = BoardUtils.toIndex(row, col);
                Piece piece = getPiece(position);

                if (piece == null)
                {
                    emptyCount++;
                }
                else
                {
                    if (emptyCount > 0)
                    {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(PieceFactory.toFENSymbol(piece));
                }
            }

            // If last squares in row are empty, add the count
            if (emptyCount > 0)
            {
                fen.append(emptyCount);
            }

            // Separate ranks by '/' except after last rank
            if (row > 0)
            {
                fen.append('/');
            }
        }

        return fen.toString();
    }

    public void importFEN(String fen)
    {
        boardMap = new HashingDynamic<>();

        whiteKingPosition = -1;
        blackKingPosition = -1;

        String[] parts = fen.split(" ");
        String piecePlacement = parts[0];

        int row = 7;
        int col = 0;

        for (char c : piecePlacement.toCharArray())
        {
            if (c == '/')
            {
                row--;
                col = 0;
            }
            else if (Character.isDigit(c))
            {
                col += Character.getNumericValue(c);
            }
            else
            {
                int position = BoardUtils.toIndex(row, col);
                Piece piece = PieceFactory.fromFENSymbol(c);
                setPiece(position, piece);
                col++;
            }
        }

        // Fallback in case setPiece doesn't initialise the cache properly
        recalculateKingPositions();
    }

    public void printBoard()
    {
        System.out.println("  +-----------------+");

        for (int row = 7; row >= 0; row--)
        {
            System.out.print((row + 1) + " |");

            for (int col = 0; col < BOARD_WIDTH; col++)
            {
                int position = BoardUtils.toIndex(row, col);
                Piece piece = getPiece(position);
                char symbol = '.';

                if (piece != null)
                {
                    symbol = PieceFactory.toFENSymbol(piece);
                }

                System.out.print(" " + symbol);
            }
            System.out.println(" |");
        }

        System.out.println("  +-----------------+");
        System.out.println("    a b c d e f g h");
    }

    public List<Pair<String, Chessboard>> generateAllPseudolegalMoveBoards(Piece.Colour colour)
    {
        List<Pair<String, Chessboard>> legalMovesBoards = new ArrayList<>();

        for (int originPos = 0; originPos < BOARD_SIZE; originPos++)
        {
            Piece piece = getPiece(originPos);
            if (piece == null || piece.getColour() != colour) continue;

            List<Integer> targets = piece.generateMoves(originPos, this);

            for (int targetPos : targets)
            {
                String sanMove;

                boolean isCapture = isOccupied(targetPos) && getPiece(targetPos).getColour() != colour;

                // Castling detection
                if (piece.getType() == PieceType.KING && Math.abs(targetPos - originPos) == 2)
                {
                    sanMove = (targetPos > originPos) ? "O-O" : "O-O-O";
                }
                else
                {
                    String toSquare = BoardUtils.toCoordinate(targetPos);
                    String pieceSymbol = PieceFactory.toAlgebraicNotation(piece);

                    String disambiguation = "";

                    if (piece.getType() != PieceType.PAWN)
                    {
                        if (needsDisambiguation(originPos, targetPos, piece))
                        {
                            disambiguation = getDisambiguation(originPos, targetPos, piece);
                        }
                    }

                    if (piece.getType() == PieceType.PAWN)
                    {
                        if (isCapture)
                        {
                            char fromFileChar = (char) ('a' + (originPos % BOARD_WIDTH));
                            sanMove = fromFileChar + "x" + toSquare;
                        }
                        else
                        {
                            sanMove = toSquare;
                        }
                        // TODO add promotion handling
                    }
                    else
                    {
                        sanMove = pieceSymbol + disambiguation + (isCapture ? "x" : "") + toSquare;
                    }

                    // TODO add check or mate indicators
                }

                Chessboard newBoard = new Chessboard();
                newBoard.importFEN(this.toFEN());

                newBoard.move(originPos, targetPos);

                legalMovesBoards.add(new Pair<>(sanMove, newBoard));
            }
        }

        return legalMovesBoards;
    }

    public List<String> generateAllPsuedolegalMoveSAN(Piece.Colour colour)
    {
        List<Pair<String, Chessboard>> legalMovesBoards = generateAllPseudolegalMoveBoards(colour);
        List<String> legalMovesSAN = new ArrayList<>();

        for (Pair<String, Chessboard> pair : legalMovesBoards)
        {
            legalMovesSAN.add(pair.getKey());
        }

        return legalMovesSAN;
    }

    public List<Pair<String, Chessboard>> generateAllLegalMoveBoards(Piece.Colour colour)
    {
        List<Pair<String, Chessboard>> pseudolegalMovePairs = generateAllPseudolegalMoveBoards(colour);
        List<Pair<String, Chessboard>> legalMovePairs = new ArrayList<>();

        for (Pair<String, Chessboard> movePair : pseudolegalMovePairs)
        {
            Chessboard resultingBoard = movePair.getValue();

            if (!resultingBoard.isInCheck(colour))
            {
                legalMovePairs.add(movePair);
            }
        }

        return legalMovePairs;
    }

    public List<String> generateAllLegalMoveSAN(Piece.Colour colour)
    {
        List<Pair<String, Chessboard>> legalMovesBoards = generateAllLegalMoveBoards(colour);
        List<String> legalMovesSAN = new ArrayList<>();

        for (Pair<String, Chessboard> pair : legalMovesBoards)
        {
            legalMovesSAN.add(pair.getKey());
        }

        return legalMovesSAN;
    }

    public int getKingPosition(Piece.Colour colour)
    {
        return (colour == Piece.Colour.WHITE) ? whiteKingPosition : blackKingPosition;
    }

    public void recalculateKingPositions()
    {
        whiteKingPosition = -1;
        blackKingPosition = -1;

        for (int position = 0; position < BOARD_SIZE; position++)
        {
            Piece piece = getPiece(position);

            if (piece != null && piece.getType() == PieceType.KING) whiteKingPosition = position;
            else blackKingPosition = position;
        }
    }

    public boolean isInCheck(Piece.Colour colour)
    {
        int kingPos = getKingPosition(colour);
        if (kingPos == -1) return true; // If the king is missing, treat it as check

        Piece.Colour opponent = (colour == Piece.Colour.WHITE) ? Piece.Colour.BLACK : Piece.Colour.WHITE;

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            Piece p = getPiece(i);
            if (p != null && p.getColour() == opponent)
            {
                List<Integer> attacks = p.generateMoves(i, this);
                if (attacks.contains(kingPos)) return true;
            }
        }

        return false;
    }

    private boolean needsDisambiguation(int originPos, int targetPos, Piece piece)
    {
        for (int pos = 0; pos < BOARD_SIZE; pos++)
        {
            if (pos == originPos) continue;
            Piece other = getPiece(pos);

            if (other != null && other.getColour() == piece.getColour() && other.getType() == piece.getType())
            {
                List<Integer> otherMoves = other.generateMoves(pos, this);
                if (otherMoves.contains(targetPos)) return true;
            }
        }
        return false;
    }

    private String getDisambiguation(int originPos, int targetPos, Piece piece)
    {
        int originFile = BoardUtils.getFile(originPos);
        int originRank = BoardUtils.getRank(originPos);

        List<Integer> others = new ArrayList<>();

        for (int pos = 0; pos < BOARD_SIZE; pos++)
        {
            if (pos == originPos) continue;
            Piece other = getPiece(pos);
            if (other != null && other.getColour() == piece.getColour() && other.getType() == piece.getType())
            {
                others.add(pos);
            }
        }

        if (others.isEmpty()) return "";

        boolean sameFile = false;
        boolean sameRank = false;

        for (int pos : others)
        {
            if ((BoardUtils.getFile(pos)) == originFile) sameFile = true;
            if ((BoardUtils.getRank(pos)) == originRank) sameRank = true;
        }

        if (!sameFile) return "" + (char)('a' + originFile);
        if (!sameRank) return "" + (char)('1' + originRank);
        return BoardUtils.toCoordinate(originPos);
    }
}