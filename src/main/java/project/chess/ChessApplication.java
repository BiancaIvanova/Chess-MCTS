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
		String startFEN = "6Rk/8/5N2/8/8/8/8/7K";
		board.importFEN(startFEN);

		// Print the board
		board.printBoard();

		// Generate all legal moves for white
		System.out.println("\nLegal moves for Black:");
		List<String> whiteMoves = board.generateAllLegalMoveSAN(Piece.Colour.BLACK);
		for (String move : whiteMoves) {
			System.out.println(move);
		}
		System.out.println("\nNumber of calculated moves: " + whiteMoves.size());
	}
}
