package project.chess;

import project.chess.pieces.Piece;

public class PieceFactory
{
    public static Piece copy(Piece piece)
    {
        if (piece == null) return null;
        return piece.getType().create(piece.getColour());
    }


    public static Piece fromFENSymbol(char c)
    {
        Piece.Colour colour = Character.isUpperCase(c) ? Piece.Colour.WHITE : Piece.Colour.BLACK;
        PieceType type = PieceType.fromFENSymbol(c);
        return type.create(colour);
    }

    public static char toFENSymbol(Piece piece)
    {
        return piece.getType().getFenSymbol(piece.getColour());
    }

    public static String toAlgebraicNotation(Piece piece)
    {
        return piece.getType().getAlgebraic();
    }
}
