package project.chess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import project.chess.pieces.*;

import java.util.List;

@SpringBootApplication

public class ChessApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ChessApplication.class, args);

		Chessboard board = new Chessboard();

		// Start position FEN
		String startFEN = "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1";
		//String startFEN = "8/8/8/8/8/8/8/R3K2R";
		board.importBasicFEN(startFEN);

		// Print the board
		board.printBoard();

		// Generate all legal moves for white
		System.out.println("\nLegal moves for White:");
		List<String> whiteMoves = board.generateAllLegalMoveSAN(Piece.Colour.WHITE);
		for (String move : whiteMoves) {
			System.out.println(move);
		}
		System.out.println("\nNumber of calculated moves: " + whiteMoves.size());
	}
}
