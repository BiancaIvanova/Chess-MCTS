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
		board.importBasicFEN("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1");

		System.out.println("Initial Position:");
		board.printBoard();

		long  startTime = System.currentTimeMillis();

		for (int depth = 1; depth <= 10; depth++) {
			long nodes = Perft.perft(board, depth, Piece.Colour.WHITE);
			long elapsedMillis = System.currentTimeMillis() - startTime;
			System.out.printf("Depth %d: %d nodes  \t\tTime elapsed: %.3f seconds\n", depth, nodes, elapsedMillis / 1000.0);
		}
	}
}
