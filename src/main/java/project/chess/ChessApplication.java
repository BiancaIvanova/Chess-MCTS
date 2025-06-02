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
		Rook blackRook = new Rook(Piece.Colour.BLACK);
		Bishop blackBishop = new Bishop(Piece.Colour.BLACK);
		Queen blackQueen = new Queen(Piece.Colour.BLACK);
		King blackKing = new King(Piece.Colour.BLACK);
		Pawn whitePawn = new Pawn(Piece.Colour.WHITE);
		Pawn blackPawn = new Pawn(Piece.Colour.BLACK);

		int position = 18;

		board.setPiece(position, blackPawn);
		board.setPiece(11, whiteKnight);

		List<Integer> moves = blackPawn.generateMoves(position, board);

		System.out.println("Moves from position " + position + ":");

		for (int move : moves)
		{
			System.out.println(move);
		}
	}
}
