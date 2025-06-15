package project.chess;

import static project.chess.Chessboard.BOARD_WIDTH;

public class BoardUtils
{
    /**
     * Returns the file (column), from 0-7, given a square index from 0-63.
     */
    public static int getFile(int position)
    {
        return position % BOARD_WIDTH;
    }

    /**
     * Returns the rank (row), from 0-7, given a square index from 0-63.
     */
    public static int getRank(int position)
    {
        return position / BOARD_WIDTH;
    }

    /**
     * Converts a rank (row) and file (column), each from 0-7, into a square index from 0-63.
     */
    public static int toIndex(int rank, int file)
    {
        return ( rank * BOARD_WIDTH ) + file;
    }

    /**
     * Converts a square coordinate in algebraic notation (ie. e5) into an index from 0-63.
     */
    public static int toIndex(String square)
    {
        if (square == null || square.length() != 2)
            throw new IllegalArgumentException("Invalid square coordinate: " + square);

        char fileChar = square.charAt(0);
        char rankChar = square.charAt(1);

        int file = fileChar - 'a';
        int rank = rankChar - '1';

        if (file < 0 || file >= BOARD_WIDTH || rank < 0 || rank >= BOARD_WIDTH)
            throw new IllegalArgumentException("Coordinate out of bounds: " + square);

        return toIndex(rank, file);
    }

    /**
     * Converts an index from 0-63 into a square coordinate in algebraic notation (ie. e5)
     */
    public static String toCoordinate(int position)
    {
        char fileChar = (char) ('a' + getFile(position));
        char rankChar = (char) ('1' + getRank(position));
        return "" + fileChar + rankChar;
    }
}
