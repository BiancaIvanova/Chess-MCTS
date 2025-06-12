package project.chess;

import static project.chess.Chessboard.BOARD_WIDTH;

public class BoardUtils
{
    // Returns the file, from 0-7
    public static int getFile(int position)
    {
        return position % BOARD_WIDTH;
    }

    public static int getRank(int position)
    {
        return position / BOARD_WIDTH;
    }

    public static int toIndex(int rank, int file)
    {
        return ( rank * BOARD_WIDTH ) + file;
    }

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

    public static String toCoordinate(int position)
    {
        char fileChar = (char) ('a' + getFile(position));
        char rankChar = (char) ('1' + getRank(position));
        return "" + fileChar + rankChar;
    }
}
