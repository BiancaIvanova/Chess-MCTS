package project.chess;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece
{
    public Queen(Piece.Colour colour) { super(colour); }

    @Override
    public char getNotationSymbol() {
        return 'Q';
    }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        List<Integer> moves = new ArrayList<>();

        int[] directions = {-9, -8, -7, -1, 1, 7, 8, 9};

        for (int dir : directions)
        {
            int target = position;
            boolean canContinue = true;

            for (int step = 1; step < 8 && canContinue; step++)
            {
                int prevCol = target % 8;
                target += dir;

                if (target < 0 || target >= 64)
                {
                    canContinue = false; // off board
                }
                else
                {
                    int currentCol = target % 8;

                    // Check if column jumped more than one step
                    if (Math.abs(currentCol - prevCol) > 1)
                    {
                        canContinue = false;
                    }
                    else
                    {
                        Piece targetPiece = board.getPiece(target);

                        if (targetPiece == null)
                        {
                            moves.add(target);
                        }
                        else
                        {
                            if (targetPiece.getColour() != this.getColour())
                            {
                                moves.add(target); // capture
                            }
                            canContinue = false;
                        }
                    }
                }

            }
        }

        return moves;
    }
}
