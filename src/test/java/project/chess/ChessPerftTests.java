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
        System.out.printf("Running Perft test | Depth %d | Side %s\n", depth, sideToMove);
        System.out.printf("FEN: %s\n", fen);
        long actualNodes = Perft.perftFromFEN(fen, depth, sideToMove);
        System.out.printf("Expected: %d | Actual nodes: %d\n\n", expectedNodes, actualNodes);
        assertEquals(expectedNodes, actualNodes,
                String.format("Failed at depth %d for FEN: %s", depth, fen));
    }

    @Test
    void test1InitialPosition()
    {
        System.out.println("TEST 1: Initial Position");
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        assertPerft(fen, 1, 20, Piece.Colour.WHITE);
        assertPerft(fen, 2, 400, Piece.Colour.WHITE);
        assertPerft(fen, 3, 8902, Piece.Colour.WHITE);
    }

    @Test
    void test2Kiwipete()
    {
        System.out.println("TEST 2: Kiwipete");
        String fen = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R";
        assertPerft(fen, 1, 48, Piece.Colour.WHITE);
        assertPerft(fen, 2, 2039, Piece.Colour.WHITE);
        assertPerft(fen, 3, 97862, Piece.Colour.WHITE);
    }

    @Test
    void test3SparseBoard()
    {
        System.out.println("TEST 3: Sparse board");
        String fen = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8";
        assertPerft(fen, 1, 14, Piece.Colour.WHITE);
        assertPerft(fen, 2, 191, Piece.Colour.WHITE);
        assertPerft(fen, 3, 2812, Piece.Colour.WHITE);
        assertPerft(fen, 4, 43238, Piece.Colour.WHITE);
    }

    @Test
    void test4OtherBoard()
    {
        System.out.println("TEST 4: Other board");
        String fen = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1";
        assertPerft(fen, 1, 6, Piece.Colour.WHITE);
        assertPerft(fen, 2, 264, Piece.Colour.WHITE);
        assertPerft(fen, 3, 9467, Piece.Colour.WHITE);
        assertPerft(fen, 4, 422333, Piece.Colour.WHITE);
    }

    @Test
    void test5TrompTraxlerTest()
    {
        System.out.println("TEST 5: Tromp-Traxler Test (Promotion Heavy)");
        String fen = "n1n5/PPPk4/8/8/8/8/4Kppp/5N1N";
        assertPerft(fen, 1, 24, Piece.Colour.WHITE);
        assertPerft(fen, 2, 496, Piece.Colour.WHITE);
        assertPerft(fen, 3, 9483, Piece.Colour.WHITE);
    }
}
