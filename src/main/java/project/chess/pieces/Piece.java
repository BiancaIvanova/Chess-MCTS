package project.chess.pieces;

import lombok.Getter;
import project.chess.model.BoardUtils;
import project.chess.model.Chessboard;
import project.chess.model.PieceFactory;
import project.chess.model.PieceType;

import java.util.ArrayList;
import java.util.List;

import static project.chess.model.Chessboard.BOARD_SIZE;

@Getter
public abstract class Piece
{
    protected static final int[] DIAGONAL_DIRECTIONS = {-9, -7, 7, 9};
    protected static final int[] HORIZONTAL_DIRECTIONS = {-1, 1};

    public enum Colour
    {
        WHITE,
        BLACK;
    }

    protected final Colour colour;

    public Piece(Colour colour) { this.colour = colour; }

    // Copy constructor
    public Piece(Piece other) { this.colour = other.colour; }

    protected List<Integer> generateSlidingMoves(int position, Chessboard board, int[] directions)
    {
        List<Integer> moves = new ArrayList<>();

        for (int dir : directions)
        {
            int target = position;

            while (true)
            {
                int prevCol = BoardUtils.getFile(target);
                target += dir;

                if (!isOnBoard(target)) break;

                int currentCol = BoardUtils.getFile(target);
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
        return ( dir == HORIZONTAL_DIRECTIONS[0] || dir == HORIZONTAL_DIRECTIONS[1]);
    }

    protected boolean isWrapping(int prevCol, int currentCol, int dir)
    {
        int colDelta = Math.abs(currentCol - prevCol);

        // Horizontal
        if (isHorizontal(dir)) return colDelta != 1;

        // Diagonals
        for (int d : DIAGONAL_DIRECTIONS) if (dir == d) return colDelta != 1;

        // Vertical moves don't wrap by columns
        return false;
    }

    public abstract List<Integer> generateMoves(int position, Chessboard board);

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
