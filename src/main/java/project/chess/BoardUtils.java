package project.chess;

import static project.chess.Chessboard.BOARD_WIDTH;

public class BoardUtils
{
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

    public static String toCoordinate(int position)
    {
        char fileChar = (char) ('a' + getFile(position));
        char rankChar = (char) ('1' + getRank(position));
        return "" + fileChar + rankChar;
    }
}
