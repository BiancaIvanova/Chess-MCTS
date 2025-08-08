package project.chess.mcts;

import project.chess.Chessboard;
import project.chess.PieceType;
import project.chess.pieces.Piece;

import static project.chess.Chessboard.BOARD_WIDTH;
import static project.chess.Chessboard.BOARD_SIZE;

public class HeuristicEvaluator
{
    public static final int PAWN_VALUE = 1;
    public static final int KNIGHT_VALUE = 3;
    public static final int BISHOP_VALUE = 3;
    public static final int ROOK_VALUE = 5;
    public static final int QUEEN_VALUE = 9;
    public static final int KING_VALUE = 100;

    private static final double MOBILITY_FACTOR = 0.02;
    private static final double PAWN_ADVANCEMENT_FACTOR = 0.1;

    public double evaluate(Chessboard board, Piece.Colour playerColour)
    {
        double score = 0;

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            Piece piece = board.getPiece(i);
            if (piece == null) continue;

            int baseValue = getPieceValue(piece);
            double pieceScore = baseValue;

            // Pawn advancement bonus
            if (piece.getType() == PieceType.PAWN) {
                int rank = i / BOARD_WIDTH; // 0 = top row, 7 = bottom row
                double progress = getPawnProgress(rank, piece.getColour());
                pieceScore += progress * PAWN_ADVANCEMENT_FACTOR;
            }

            if (piece.getColour() == playerColour) score += pieceScore;
            else score -= pieceScore;
        }

        score += mobilityBonus(board, playerColour);

        return score;
    }

    private double mobilityBonus(Chessboard board, Piece.Colour playerColour)
    {
        int playerMobility = board.generateAllLegalMoveBoards(playerColour).size();

        Piece.Colour opponent = (playerColour == Piece.Colour.WHITE) ? Piece.Colour.BLACK : Piece.Colour.WHITE;
        int opponentMobility = board.generateAllLegalMoveBoards(opponent).size();

        return (playerMobility - opponentMobility) * MOBILITY_FACTOR;
    }

    private int getPieceValue(Piece piece) {
        switch(piece.getType()) {
            case PAWN:   return PAWN_VALUE;
            case KNIGHT: return KNIGHT_VALUE;
            case BISHOP: return BISHOP_VALUE;
            case ROOK:   return ROOK_VALUE;
            case QUEEN:  return QUEEN_VALUE;
            case KING:   return KING_VALUE;
            default:     return 0;
        }
    }

    private double getPawnProgress(int rank, Piece.Colour colour)
    {
        if (colour == Piece.Colour.WHITE) return (6 - rank) / 6.0;
        else return (rank - 1) / 6.0;
    }
}
