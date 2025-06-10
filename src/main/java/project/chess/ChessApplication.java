package project.chess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import project.chess.pieces.*;
import project.chess.test.Perft;

import java.util.List;

@SpringBootApplication

public class ChessApplication
{
	public static void main(String[] args)
	{
		//SpringApplication.run(ChessApplication.class, args);

		Chessboard board = new Chessboard();
		board.importBasicFEN("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1");

		System.out.println("Initial Position:");
		board.printBoard();

		long  startTime = System.currentTimeMillis();

		for (int depth = 1; depth <= 10; depth++) {
			long nodes = Perft.perft(board, depth, Piece.Colour.WHITE);
			long elapsedMillis = System.currentTimeMillis() - startTime;
			System.out.printf("Depth %d: %d nodes  \t\tTime elapsed: %.3f seconds\n", depth, nodes, elapsedMillis / 1000.0);
		}

//		Chessboard board = new Chessboard();
//
//		// Start position FEN
//		String startFEN = "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1";
//		//String startFEN = "8/8/8/8/8/8/8/R3K2R";
//		board.importBasicFEN(startFEN);
//
//		// Print the board
//		board.printBoard();
//
//		// Generate all legal moves for white
//		System.out.println("\nLegal moves for White:");
//		List<String> whiteMoves = board.generateAllLegalMoveSAN(Piece.Colour.WHITE);
//		for (String move : whiteMoves) {
//			System.out.println(move);
//		}
//		System.out.println("\nNumber of calculated moves: " + whiteMoves.size());
	}
}
