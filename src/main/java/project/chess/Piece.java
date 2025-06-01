package project.chess;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece
{
    protected static final int BOARD_SIZE = 64;
    protected static final int BOARD_WIDTH = 8;
    protected static final int BOARD_HEIGHT = 8;

    public enum Colour
    {
        WHITE,
        BLACK;
    }

    protected final Colour colour;

    protected List<Integer> generateSlidingMoves(int position, Chessboard board, int[] directions)
    {
        List<Integer> moves = new ArrayList<>();

        for (int dir : directions)
        {
            int target = position;

            while (true)
            {
                int prevCol = target % BOARD_WIDTH;
                target += dir;

                if (!isOnBoard(target)) break;

                int currentCol = target % BOARD_WIDTH;
                if (isWrapping(prevCol, currentCol, dir)) break;

                Piece targetPiece = board.getPiece(target);

                if (targetPiece == null)
                {
                    moves.add(target);
                }
                else
                {
                    if (targetPiece.getColour() != this.getColour())
                    {
                        moves.add(target); // Capturing an opponent piece
                    }
                    break; // Blocked by a friendly piece
                }
            }
        }

        return moves;
    }

    protected boolean isOnBoard(int pos)
    {
        return ( pos >= 0 && pos < BOARD_SIZE );
    }

    protected boolean isHorizontal(int dir)
    {
        return ( dir == -1 || dir == 1);
    }

    protected boolean isWrapping(int prevCol, int currentCol, int dir)
    {
        int colDelta = Math.abs(currentCol - prevCol);

        // Horizontal
        if (isHorizontal(dir)) return colDelta != 1;

        // Diagonals
        if (dir == -9 || dir == -7 || dir == 7 || dir == 9) return colDelta != 1;

        // Vertical moves don't wrap by columns
        return false;
    }

    public Piece(Colour colour) { this.colour = colour; }

    public Colour getColour() { return this.colour; }

    public abstract char getNotationSymbol();


    public abstract List<Integer> generateMoves(int position, Chessboard board);

    @Override
    public String toString() {return this.getNotationSymbol() + ""; }
}
