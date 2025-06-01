package project.chess;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece
{
    public Bishop(Colour colour) { super(colour); }

    @Override
    public char getNotationSymbol() {
        return 'B';
    }

    @Override
    public List<Integer> generateMoves(int position, Chessboard board)
    {
        List<Integer> moves = new ArrayList<>();
        int[] directions = {-9, -7, 7, 9};

        for (int dir : directions)
        {
            int currentPos = position;
            boolean canContinue = true;

            for (int step = 1; step < 8 && canContinue; step++)
            {
                int prevCol = currentPos % 8;
                currentPos += dir;

                if (currentPos < 0 || currentPos >= 64)
                {
                    canContinue = false; // off board
                }
                else
                {
                    int currentCol = currentPos % 8;

                    if (Math.abs(currentCol - prevCol) != 1)
                    {
                        canContinue = false; // wrapped around edge
                    }
                    else
                    {
                        Piece targetPiece = board.getPiece(currentPos);

                        if (targetPiece == null)
                        {
                            moves.add(currentPos);
                        }
                        else
                        {
                            if (targetPiece.getColour() != this.getColour())
                            {
                                moves.add(currentPos); // capture
                            }
                            canContinue = false; // blocked by piece
                        }
                    }
                }
            }
        }

        return moves;
    }
}
