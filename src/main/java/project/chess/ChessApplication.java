package project.chess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication

public class ChessApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ChessApplication.class, args);

		Chessboard board = new Chessboard();
		Knight whiteKnight = new Knight(Piece.Colour.WHITE);
		int position = 9;

		board.setPiece(position, whiteKnight);
		board.setPiece(26, whiteKnight);

		List<Integer> moves = whiteKnight.generateMoves(position, board);

		System.out.println("Knight moves from position " + position + ":");

		for (int move : moves)
		{
			System.out.println(move);
		}
	}

}
