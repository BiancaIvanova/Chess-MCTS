package project.chess;

import lombok.Getter;
import lombok.Setter;
import project.chess.datastructures.*;
import project.chess.pieces.Piece;

import java.util.*;

public class Chessboard
{
    public static final int BOARD_WIDTH = 8;
    public static final int BOARD_SIZE = 64;

    // King position cache
    private int whiteKingPosition = -1;
    private int blackKingPosition = -1;

    // En passant target
    @Setter
    @Getter
    private int enPassantTarget = -1;

    private IHashDynamic<Integer, Piece> boardMap;

    public EnumSet<CastlingRight> castlingRights;

    public Chessboard()
    {
        boardMap = new HashingDynamic<>();
        castlingRights = EnumSet.allOf(CastlingRight.class);
    }

    // Copy constructor
    public Chessboard(Chessboard other)
    {
        this.boardMap = new HashingDynamic<>();

        for (int position = 0; position < BOARD_SIZE; position++)
        {
            Piece piece = other.getPiece(position);
            if (piece != null)
            {
                this.boardMap.add(position, PieceFactory.copy(piece));
            }
        }

        this.whiteKingPosition = other.whiteKingPosition;
        this.blackKingPosition = other.blackKingPosition;

        this.enPassantTarget = other.enPassantTarget;
        this.castlingRights = EnumSet.copyOf(other.castlingRights);

        recalculateKingPositions();
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
            boardMap.delete(position);
        }
        else
        {
            if (oldPiece != null)
            {
                boardMap.delete(position);
            }
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

        // Handle en passant capture
        if (movingPiece.getType() == PieceType.PAWN && to == enPassantTarget)
        {
            int direction = (movingPiece.getColour() == Piece.Colour.WHITE) ? -1 : 1;
            int capturedPawnPos = to + (8 * direction);
            setPiece(capturedPawnPos, null);
        }

        // Reset en passant by default
        enPassantTarget = -1;

        // Detect if this pawn move enables en passant next turn
        if (movingPiece.getType() == PieceType.PAWN && Math.abs(to - from) == 16)
        {
            enPassantTarget = (from + to) / 2;
        }

        // TODO Promotion
    }

    private void updateCastlingRights(int from, int to)
    {
        Piece movingPiece = getPiece(from);

        // Remove castling rights if a king moves
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

        // Remove castling rights if a rook moves from or is captured on its original square
        if (movingPiece.getType() == PieceType.ROOK)
        {
            removeCastlingRightsBySquare(from);
        }

        Piece capturedPiece = getPiece(to);
        if (capturedPiece != null && capturedPiece.getType() == PieceType.ROOK)
        {
            removeCastlingRightsBySquare(to);
        }
    }

    private void removeCastlingRightsBySquare(int square)
    {
        if (square == BoardUtils.toIndex(0, 0))
        {   // a1
            castlingRights.remove(CastlingRight.WHITE_QUEENSIDE);
        }
        else if (square == BoardUtils.toIndex(0, 7))
        {   // h1
            castlingRights.remove(CastlingRight.WHITE_KINGSIDE);
        }
        else if (square == BoardUtils.toIndex(7, 0))
        {   // a8
            castlingRights.remove(CastlingRight.BLACK_QUEENSIDE);
        }
        else if (square == BoardUtils.toIndex(7, 7))
        {   // h8
            castlingRights.remove(CastlingRight.BLACK_KINGSIDE);
        }
    }

    public boolean isOccupied(int position)
    {
        return boardMap.contains(position);
    }

    public String toBasicFEN()
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

            // If the last squares in row are empty, add the count
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

    public void importBasicFEN(String fen)
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
                Piece targetPiece = getPiece(targetPos);

                boolean isCapture = isOccupied(targetPos) && targetPiece.getColour() != colour;

                if (isCapture && targetPiece.getType() == PieceType.KING) continue;

                String sanMove;

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

                    // Disambiguation for non-pawn pieces
                    if (piece.getType() != PieceType.PAWN)
                    {
                        if (needsDisambiguation(originPos, targetPos, piece))
                        {
                            disambiguation = getDisambiguation(originPos, targetPos, piece);
                        }
                    }

                    // Pawn move handling (including en passant)
                    if (piece.getType() == PieceType.PAWN)
                    {
                        boolean isEnPassant = false;

                        // En passant
                        if (targetPos == getEnPassantTarget() && !isOccupied(targetPos))
                        {
                            isEnPassant = true;
                            isCapture = true;
                        }


                        if (isCapture)
                        {
                            char fromFileChar = (char) ('a' + (originPos % BOARD_WIDTH));
                            sanMove = fromFileChar + "x" + toSquare;
                        }
                        else
                        {
                            sanMove = toSquare;
                        }

                        if ((BoardUtils.getRank(targetPos) == 0 && piece.getColour() == Piece.Colour.BLACK) ||
                                (BoardUtils.getRank(targetPos) == 7 && piece.getColour() == Piece.Colour.WHITE))
                        {
                            legalMovesBoards.addAll(generatePromotionMoves(originPos, targetPos, piece, isCapture));
                            continue;
                        }

                    }
                    else
                    {
                        sanMove = pieceSymbol + disambiguation + (isCapture ? "x" : "") + toSquare;
                    }

                    // TODO add check or mate indicators
                }

                Chessboard newBoard = new Chessboard(this);
                newBoard.move(originPos, targetPos);
                legalMovesBoards.add(new Pair<>(sanMove, newBoard));
            }
        }

        return legalMovesBoards;
    }


    public List<String> generateAllPseudolegalMoveSAN(Piece.Colour colour)
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

    private List<Pair<String, Chessboard>> generatePromotionMoves(int originPos, int targetPos, Piece movingPawn, boolean isCapture)
    {
        List<Pair<String, Chessboard>> promotionMoves = new ArrayList<>();
        char fromFileChar = (char) ('a' + (originPos % BOARD_WIDTH));
        String toSquare = BoardUtils.toCoordinate(targetPos);

        PieceType[] promotionTypes = {
                PieceType.QUEEN,
                PieceType.ROOK,
                PieceType.BISHOP,
                PieceType.KNIGHT
        };

        for (PieceType promotionType : promotionTypes)
        {
            String san;

            if (isCapture)
            {
                san = fromFileChar + "x" + toSquare + "=" + promotionType.getAlgebraic();
            }
            else
            {
                san = toSquare + "=" + promotionType.getAlgebraic();
            }

            Chessboard newBoard = new Chessboard(this);
            newBoard.setPiece(originPos, null);

            Piece promotedPiece = promotionType.create(movingPawn.getColour());
            newBoard.setPiece(targetPos, promotedPiece);

            // TODO newBoard.setEnPassantSquare(null);

            promotionMoves.add(new Pair<>(san, newBoard));
        }

        return promotionMoves;
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

            if (piece != null && piece.getType() == PieceType.KING)
            {
                if (piece.getColour() == Piece.Colour.WHITE) whiteKingPosition = position;
                else if (piece.getColour() == Piece.Colour.BLACK) blackKingPosition = position;
            }
        }
    }

    public boolean isInCheck(Piece.Colour colour)
    {
        int kingPos = getKingPosition(colour);
        if (kingPos == -1) return true; // If the king is missing, treat it as a check

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

    public boolean isCheckmate(Piece.Colour colour)
    {
        return isInCheck(colour) && generateAllLegalMoveBoards(colour).isEmpty();
    }

    public boolean isStalemate(Piece.Colour colour)
    {
        return !isInCheck(colour) && generateAllLegalMoveBoards(colour).isEmpty();
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

    public Set<CastlingRight> getCastlingRights() { return castlingRights; }

    public void setCastlingRights(Set<CastlingRight> rights)
    {
        castlingRights.clear();
        castlingRights.addAll(rights);
    }

    public int getEnPassantTarget() { return enPassantTarget; }

    public void setEnPassantTarget(int target) { enPassantTarget = target; }

    public Map<String, List<String>> getAllPieceMovesAsMap()
    {
        Map<String, List<String>> moveMap = new HashMap<>();

        for (int position = 0; position < BOARD_SIZE; position++)
        {
            Piece piece = getPiece(position);
            if (piece != null)
            {
                List<Integer> moves = piece.generateMoves(position, this);
                if (!moves.isEmpty())
                {
                    String fromCoordinate = BoardUtils.toCoordinate(position);
                    List<String> toCoordinate = new ArrayList<>();

                    for (int move : moves)
                    {
                        toCoordinate.add(BoardUtils.toCoordinate(move));
                    }

                    moveMap.put(fromCoordinate, toCoordinate);
                }
            }
        }

        return moveMap;
    }
}