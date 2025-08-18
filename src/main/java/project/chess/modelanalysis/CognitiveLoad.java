package project.chess.modelanalysis;

import project.chess.model.Chessboard;
import project.chess.piece.Piece;
import project.chess.model.PieceType;

import java.util.List;

public class CognitiveLoad {

    // Weights for different metrics
    private static final double BRANCHING_WEIGHT = 0.2;
    private static final double FORCING_WEIGHT = 0.25;
    private static final double TACTICAL_WEIGHT = 0.2;
    private static final double QUIET_WEIGHT = 0.15;
    private static final double KING_SAFETY_WEIGHT = 0.2;

    public static double computeLoad(Chessboard board, Piece.Colour colour) {

        double branchingScore = computeBranchingFactor(board, colour);
        double forcingScore = computeForcingMoves(board, colour);
        double tacticalScore = computeTacticalMotifs(board, colour);
        double quietScore = computeQuietMoveRarity(board, colour);
        double kingScore = computeKingThreats(board, colour);

        double rawScore = BRANCHING_WEIGHT * branchingScore
                + FORCING_WEIGHT * forcingScore
                + TACTICAL_WEIGHT * tacticalScore
                + QUIET_WEIGHT * quietScore
                + KING_SAFETY_WEIGHT * kingScore;

        return Math.min(100, rawScore * 100); // scale to 0-100
    }

    // The number of legal moves that exist
    private static double computeBranchingFactor(Chessboard board, Piece.Colour colour)
    {
        List<?> legalMoves = board.generateAllLegalMoveBoards(colour);
        return Math.tanh(legalMoves.size() / 40.0); // normalize roughly to 0-1
    }

    // The number of forcing moves that exist
    private static double computeForcingMoves(Chessboard board, Piece.Colour colour) {
        List<String> moves = board.generateAllLegalMoveSAN(colour);
        int forcingCount = 0;
        for (String san : moves) {
            if (san.contains("x") || san.contains("+")) forcingCount++;
        }
        return moves.isEmpty() ? 0 : (double) forcingCount / moves.size();
    }

    // Count simple tactical motifs: forks, pins, skewers
    private static double computeTacticalMotifs(Chessboard board, Piece.Colour colour) {
        int motifs = 0;
        for (int pos = 0; pos < Chessboard.BOARD_SIZE; pos++) {
            Piece p = board.getPiece(pos);
            if (p == null || p.getColour() != colour) continue;

            motifs += countForks(board, p, pos);
            motifs += countPinsAndSkewers(board, p, pos);
        }
        return Math.tanh(motifs / 5.0); // normalize
    }

    // Fraction of moves that are "quiet" (non-captures, non-checks)
    private static double computeQuietMoveRarity(Chessboard board, Piece.Colour colour) {
        List<String> moves = board.generateAllLegalMoveSAN(colour);
        int quietCount = 0;
        for (String san : moves) {
            if (!san.contains("x") && !san.contains("+")) quietCount++;
        }
        return moves.isEmpty() ? 0 : 1 - ((double) quietCount / moves.size());
    }

    // Count attacks on the king
    private static double computeKingThreats(Chessboard board, Piece.Colour colour) {
        int kingPos = board.getKingPosition(colour);
        if (kingPos == -1) return 1.0; // king missing → max load

        Piece.Colour opponent = (colour == Piece.Colour.WHITE) ? Piece.Colour.BLACK : Piece.Colour.WHITE;
        int threats = 0;

        for (int pos = 0; pos < Chessboard.BOARD_SIZE; pos++) {
            Piece p = board.getPiece(pos);
            if (p != null && p.getColour() == opponent) {
                List<Integer> attackedSquares = p.generateMoves(pos, board);
                if (attackedSquares.contains(kingPos)) threats++;
            }
        }
        return Math.tanh(threats / 3.0); // normalize
    }

    private static int countForks(Chessboard board, Piece piece, int pos) {
        List<Integer> targets = piece.generateMoves(pos, board);
        int opponentPieces = 0;
        for (int sq : targets) {
            Piece target = board.getPiece(sq);
            if (target != null && target.getColour() != piece.getColour()) opponentPieces++;
        }
        return opponentPieces >= 2 ? 1 : 0;
    }

    private static int countPinsAndSkewers(Chessboard board, Piece piece, int pos) {
        int count = 0;
        PieceType type = piece.getType();
        if (type != PieceType.ROOK && type != PieceType.BISHOP && type != PieceType.QUEEN) return 0;

        int[][] directions;
        if (type == PieceType.BISHOP) directions = new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}};
        else if (type == PieceType.ROOK) directions = new int[][]{{1,0},{-1,0},{0,1},{0,-1}};
        else directions = new int[][]{{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}}; // queen

        for (int[] dir : directions) {
            boolean ownFound = false;
            boolean enemyFound = false;
            int r = pos / 8, c = pos % 8;

            while (true) {
                r += dir[0]; c += dir[1];
                if (r < 0 || r > 7 || c < 0 || c > 7) break;
                int sq = r*8 + c;
                Piece target = board.getPiece(sq);
                if (target == null) continue;
                if (target.getColour() == piece.getColour()) {
                    if (ownFound) break; // multiple own → no pin/skewer
                    ownFound = true;
                } else {
                    if (enemyFound) {
                        count++; // pin/skewer detected
                        break;
                    }
                    enemyFound = true;
                }
            }
        }
        return count;
    }
}
