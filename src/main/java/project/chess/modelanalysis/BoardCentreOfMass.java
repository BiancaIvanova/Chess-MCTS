package project.chess.modelanalysis;

import project.chess.model.Chessboard;
import project.chess.pieces.Piece;

public class BoardCentreOfMass
{
    private enum PieceWeight
    {
        PAWN(1),
        KNIGHT(3),
        BISHOP(3),
        ROOK(5),
        QUEEN(9),
        KING(4); // smaller weight to avoid domination

        private final double weight;

        PieceWeight(double weight) { this.weight = weight; }

        public double getWeight() { return weight; }

        public static double of(Piece piece)
        {
            return PieceWeight.valueOf(piece.getType().name()).getWeight();
        }
    }

    /**
     * Compute the centre of mass for a given colour
     */
    public static double[] computeCOM(Chessboard board, Piece.Colour colour, boolean useMaterialWeight)
    {
        double sumX = 0;
        double sumY = 0;
        double totalWeight = 0;

        int index = 0;

        for (Piece piece : board)
        {
            if (piece != null && piece.getColour() == colour)
            {
                int x = index % Chessboard.BOARD_WIDTH;
                int y = index / Chessboard.BOARD_WIDTH;
                double weight = useMaterialWeight ? PieceWeight.of(piece) : 1;

                sumX += x * weight;
                sumY += y * weight;
                totalWeight += weight;
            }
            index++;
        }

        if (totalWeight == 0) return new double[]{-1, -1}; // No pieces
        return new double[]{sumX / totalWeight, sumY / totalWeight};
    }

    /** Defaults to uniform weights */
    public static double[] computeCOM(Chessboard board, Piece.Colour colour)
    {
        return computeCOM(board, colour, false);
    }
}
