package project.chess.model;

import project.chess.piece.Piece;

/**
 * Factory class for creating and converting chess pieces.
 */

public class PieceFactory
{
    /**
     * Creates a copy of the given piece.
     */
    public static Piece copy(Piece piece)
    {
        if (piece == null) return null;
        return piece.getType().create(piece.getColour());
    }

    /**
     * Creates a piece from a FEN symbol (e.g., 'K' = white king, 'p' = black pawn).
     */
    public static Piece fromFENSymbol(char c)
    {
        Piece.Colour colour = Character.isUpperCase(c) ? Piece.Colour.WHITE : Piece.Colour.BLACK;
        PieceType type = PieceType.fromFENSymbol(c);
        return type.create(colour);
    }

    /**
     * Converts a piece to its FEN symbol representation.
     */
    public static char toFENSymbol(Piece piece)
    {
        return piece.getType().getFenSymbol(piece.getColour());
    }

    /**
     * Returns the algebraic notation symbol for the piece.
     */
    public static String toAlgebraicNotation(Piece piece)
    {
        return piece.getType().getAlgebraic();
    }
}
