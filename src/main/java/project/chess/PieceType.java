package project.chess;

import lombok.Getter;
import project.chess.pieces.Piece;
import project.chess.pieces.Knight;
import project.chess.pieces.Bishop;
import project.chess.pieces.Rook;
import project.chess.pieces.Queen;
import project.chess.pieces.Pawn;
import project.chess.pieces.King;

import java.util.function.Function;

public enum PieceType
{
    PAWN("P", "", Pawn::new),
    KNIGHT("N", "N", Knight::new),
    BISHOP("B", "B", Bishop::new),
    ROOK("R", "R", Rook::new),
    QUEEN("Q", "Q", Queen::new),
    KING("K", "K", King::new);

    private final String fenSymbol;
    @Getter
    private final String algebraic;
    private final Function<Piece.Colour, Piece> constructor;

    PieceType(String symbol, String algebraic, Function<Piece.Colour, Piece> constructor)
    {
        this.fenSymbol = symbol;
        this.algebraic = algebraic;
        this.constructor = constructor;
    }

    public char getFenSymbol(Piece.Colour colour)
    {
        return (colour == Piece.Colour.WHITE)   ? fenSymbol.charAt(0)
                                                : Character.toLowerCase(fenSymbol.charAt(0));
    }

    public Piece create(Piece.Colour colour)
    {
        return constructor.apply(colour);
    }

    public static PieceType fromFENSymbol(char symbol)
    {
        char upper = Character.toUpperCase(symbol);
        for (PieceType type : values())
        {
            if (type.fenSymbol.equals(String.valueOf(upper))) return type;
        }
        throw new IllegalArgumentException("Invalid FEN symbol: " + symbol);
    }
}
