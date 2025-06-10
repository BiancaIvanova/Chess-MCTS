package project.chess.test;

import project.chess.Chessboard;
import project.chess.pieces.Piece;

public class Perft
{
    public static long perft(Chessboard board, int depth, Piece.Colour sideToMove)
    {
        if (depth == 0) return 1;

        long nodes = 0;
        for (var movePair : board.generateAllLegalMoveBoards(sideToMove))
        {
            Chessboard newBoard = movePair.getValue();
            Piece.Colour nextSide = (sideToMove == Piece.Colour.WHITE) ? Piece.Colour.BLACK : Piece.Colour.WHITE;
            nodes += perft(newBoard, depth - 1, nextSide);
        }

        return nodes;
    }

    public static void divide(Chessboard board, int depth, Piece.Colour sideToMove)
    {
        long total = 0;

        for (var movePair : board.generateAllPseudolegalMoveBoards(sideToMove))
        {
            String move = movePair.getKey();
            Chessboard newBoard = movePair.getValue();
            Piece.Colour nextSide = (sideToMove == Piece.Colour.WHITE) ? Piece.Colour.BLACK : Piece.Colour.WHITE;

            long count = perft(newBoard, depth - 1, nextSide);
            total += count;
            System.out.printf("%s: %d\n", move, count);
        }

        System.out.printf("Total nodes at depth %d: %d\n", depth, total);
    }

    public static long perftFromFEN(String fen, int depth, Piece.Colour sideToMove)
    {
        Chessboard board = new Chessboard();
        board.importBasicFEN(fen);
        return perft(board, depth, sideToMove);
    }
}
