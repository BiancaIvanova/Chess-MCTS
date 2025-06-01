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
		Knight blackKnight = new Knight(Piece.Colour.BLACK);
		Rook blackRook = new Rook(Piece.Colour.BLACK);
		Bishop blackBishop = new Bishop(Piece.Colour.BLACK);
		Queen blackQueen = new Queen(Piece.Colour.BLACK);
		int position = 7;

		board.setPiece(position, blackRook);
		//board.setPiece(29, blackKnight);

		List<Integer> moves = blackRook.generateMoves(position, board);

		System.out.println("Moves from position " + position + ":");

		for (int move : moves)
		{
			System.out.println(move);
		}
	}
}
