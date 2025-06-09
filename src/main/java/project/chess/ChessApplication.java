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
		String startFEN = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R";
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
