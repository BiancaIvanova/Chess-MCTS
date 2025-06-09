package project.chess.pieces;

import project.chess.Chessboard;
import project.chess.PieceFactory;
import project.chess.PieceType;

import java.util.ArrayList;
import java.util.List;

import static project.chess.Chessboard.BOARD_SIZE;
import static project.chess.Chessboard.BOARD_WIDTH;

public abstract class Piece
{
    public enum Colour
    {
        WHITE,
        BLACK;
    }

    protected final Colour colour;

    public Piece(Colour colour) { this.colour = colour; }

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
                        moves.add(target); // Capture
                    }
                    break;
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

    public abstract List<Integer> generateMoves(int position, Chessboard board);

    public Colour getColour() { return this.colour; }

    public abstract PieceType getType();

    public char toFENSymbol()
    {
        return PieceFactory.toFENSymbol(this);
    }

    public String toAlgebraicNotation()
    {
        return PieceFactory.toAlgebraicNotation(this);
    };

    @Override
    public String toString() {return this.toFENSymbol() + ""; }
}
