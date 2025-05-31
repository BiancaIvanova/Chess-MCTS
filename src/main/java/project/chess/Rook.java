package project.chess;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece
{
    public Rook(Colour colour) { super(colour); }

    @Override
    public char getNotationSymbol() {
        return 'R';
    }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        List<Integer> moves = new ArrayList<>();
        int[] directions = {-8, -1, 1, 8};

        for (int dir : directions)
        {
            for (int target = position + dir; target >= 0 && target < 64; target+= dir)
            {
                int targetCol = target % 8;
                if ((dir == -1 && targetCol == 7) || (dir == 1 && targetCol == 0)) break;

                Piece targetSquarePiece = board.getPiece(target);

                if (targetSquarePiece == null)
                {
                    moves.add(target);
                }
                else
                {
                    if (targetSquarePiece.getColour() != this.getColour())
                    {
                        moves.add(target); // Capture
                    }
                    break;
                }
            }

        }

        return moves;
    }
}
