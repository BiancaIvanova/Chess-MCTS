package project.chess;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import project.chess.mcts.HeuristicEvaluator;
import project.chess.pieces.Piece;

@SpringBootTest
public class MCTSSimulationTests {

    private void stepThroughSimulation(String fen, Piece.Colour playerToMove) {
        System.out.println("Starting simulation for FEN: " + fen + " | Player: " + playerToMove);

        // Create initial game state from FEN
        Game game = new Game();
        game.importFEN(fen);

        Piece.Colour currentPlayer = playerToMove;
        int maxPlayoutDepth = 5; // Keep short for demonstration
        HeuristicEvaluator evaluator = new HeuristicEvaluator();

        for (int d = 0; d < maxPlayoutDepth && !game.isGameOver(); d++) {
            System.out.printf("\n--- Playout step %d | Player: %s ---\n", d + 1, currentPlayer);

            var legalMoves = game.getBoard().generateAllLegalMoveBoards(currentPlayer);
            if (legalMoves.isEmpty()) {
                System.out.println("No legal moves available. Game over condition may be reached.");
                break;
            }

            // Show all legal moves with heuristic scores
            System.out.println("Legal moves and heuristic scores:");
            for (var move : legalMoves) {
                double score = evaluator.evaluate(move.getValue(), currentPlayer);
                System.out.printf("  Move: %-6s | Heuristic: %.2f\n", move.getKey(), score);
            }

            // Pick best move according to heuristic
            var bestMove = legalMoves.get(0);
            double bestScore = Double.NEGATIVE_INFINITY;
            for (var move : legalMoves) {
                double score = evaluator.evaluate(move.getValue(), currentPlayer);
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }

            // Make the move
            game.makeMove(bestMove);

            // Output the move made
            System.out.println("Chosen move: " + bestMove.getKey() + " | Score: " + bestScore);

            // Print board state
            game.getBoard().printBoard();

            // Print FEN of current board
            System.out.println("FEN after move: " + game.getFEN());

            // Switch player
            currentPlayer = (currentPlayer == Piece.Colour.WHITE) ? Piece.Colour.BLACK : Piece.Colour.WHITE;
        }

        // Evaluate final position heuristically
        double finalScore = evaluator.evaluate(game.getBoard(), playerToMove);
        double normalizedScore = 0.5 + 0.5 * Math.tanh(finalScore / 10);
        System.out.println("\nFinal heuristic score for player " + playerToMove + ": " + normalizedScore);
    }

    @Test
    void test1InitialPositionSim() {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        stepThroughSimulation(fen, Piece.Colour.WHITE);
    }

    @Test
    void test2KiwipeteSim() {
        String fen = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1";
        stepThroughSimulation(fen, Piece.Colour.WHITE);
    }

    @Test
    void test3SparseBoardSim() {
        String fen = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1";
        stepThroughSimulation(fen, Piece.Colour.WHITE);
    }

    @Test
    void test4OtherBoardSim() {
        String fen = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w KQkq - 0 1";
        stepThroughSimulation(fen, Piece.Colour.WHITE);
    }

    @Test
    void test5TrompTraxlerSim() {
        String fen = "n1n5/PPPk4/8/8/8/8/4Kppp/5N1N w - - 0 1";
        stepThroughSimulation(fen, Piece.Colour.WHITE);
    }
}
