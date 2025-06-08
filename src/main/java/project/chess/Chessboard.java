package project.chess;

import project.chess.datastructures.HashingDynamic;
import project.chess.datastructures.IHashDynamic;
import project.chess.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Chessboard
{
    private IHashDynamic<Integer, Piece> boardMap;

    public Chessboard()
    {
        boardMap = new HashingDynamic<>();
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
        if (piece == null)
        {
            boardMap.delete(position);
        }
        else
        {
            boardMap.add(position, piece);
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
        for (int row = 7; row >= 0; row--)
        {
            int emptyCount = 0;

            for (int col = 0; col < 8; col++)
            {
                int position = ( row * 8 ) + col;
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
                int position = (row * 8) + col;
                Piece piece = PieceFactory.fromFENSymbol(c);
                setPiece(position, piece);
                col++;
            }
        }
    }

    public void printBoard()
    {
        System.out.println("  +-----------------+");

        for (int row = 7; row >= 0; row--)
        {
            System.out.print((row + 1) + " |");

            for (int col = 0; col < 8; col++)
            {
                int position = ( row * 8 ) + col;
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

    public List<String> generateAllLegalMovesSAN(Piece.Colour colour)
    {
        List<String> legalMovesSAN = new ArrayList<>();

        for (int originPos = 0; originPos < 64; originPos++)
        {
            Piece piece = getPiece(originPos);
            if (piece == null || piece.getColour() != colour) continue;

            List<Integer> targets = piece.generateMoves(originPos, this);

            for (int targetPos : targets)
            {
                boolean isCapture = isOccupied(targetPos) && getPiece(targetPos).getColour() != colour;

                // Castling detection
                if (piece.getType() == PieceType.KING && Math.abs(targetPos - originPos) == 2)
                {
                    if (targetPos > originPos)
                    {
                        legalMovesSAN.add("O-O"); // Kingside castling
                    }
                    else
                    {
                        legalMovesSAN.add("O-O-O"); // Queenside castling
                    }
                    continue;
                }

                String toSquare = posToAlgebraic(targetPos);
                String pieceSymbol = PieceFactory.toAlgebraicNotation(piece);

                String disambiguation = "";

                if (piece.getType() != PieceType.PAWN)
                {
                    if (needsDisambiguation(originPos, targetPos, piece))
                    {
                        disambiguation = getDisambiguation(originPos, targetPos, piece);
                    }
                }

                String move;

                if (piece.getType() == PieceType.PAWN)
                {
                    if (isCapture)
                    {
                        char fromFileChar = (char) ('a' + (originPos % 8));
                        move = fromFileChar + "x" + toSquare;
                    }
                    else
                    {
                        move = toSquare;
                    }
                    // TODO add promotion handling
                }
                else
                {
                    move = pieceSymbol + disambiguation + (isCapture ? "x" : "") + toSquare;
                }

                // TODO add check or mate indicators

                legalMovesSAN.add(move);
            }
        }

        return legalMovesSAN;
    }

    public static String posToAlgebraic(int pos)
    {
        int file = pos % 8;
        int rank = pos / 8;
        char fileChar = (char) ('a' + (file));
        char rankChar = (char) ('1' + (rank));
        return "" + fileChar + rankChar;
    }

    private boolean needsDisambiguation(int originPos, int targetPos, Piece piece)
    {
        for (int pos = 0; pos < 64; pos++)
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
        int originFile = originPos % 8;
        int originRank = originPos / 8;

        List<Integer> others = new ArrayList<>();

        for (int pos = 0; pos < 64; pos++)
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
            if ((pos % 8) == originFile) sameFile = true;
            if ((pos / 8) == originRank) sameRank = true;
        }

        if (!sameFile) return "" + (char)('a' + originFile);
        if (!sameRank) return "" + (char)('1' + originRank);
        return "" + (char)('a' + originFile) + (char)('1' + originRank);
    }

}
;