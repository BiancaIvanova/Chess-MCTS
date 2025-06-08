package project.chess;

import project.chess.datastructures.HashingDynamic;
import project.chess.datastructures.IHashDynamic;
import project.chess.pieces.Bishop;
import project.chess.pieces.Piece;

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
}
