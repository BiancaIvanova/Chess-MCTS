package project.chess;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import project.chess.pieces.Piece;
import project.chess.test.Perft;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ChessPerftTests
{
    private void assertPerft(String fen, int depth, long expectedNodes, Piece.Colour sideToMove)
    {
        long actualNodes = Perft.perftFromFEN(fen, depth, sideToMove);
        assertEquals(expectedNodes, actualNodes,
                String.format("Failed at depth %d for FEN: %s", depth, fen));
    }

    @Test
    void test1InitialPosition()
    {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        assertPerft(fen, 1, 20, Piece.Colour.WHITE);
        assertPerft(fen, 2, 400, Piece.Colour.WHITE);
        assertPerft(fen, 3, 8902, Piece.Colour.WHITE);
    }
}
