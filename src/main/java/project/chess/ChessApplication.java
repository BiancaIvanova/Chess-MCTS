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

		Chessboard board =  new Chessboard();

		board.setPiece(56, new Rook(Piece.Colour.BLACK));
		board.setPiece(57, new Knight(Piece.Colour.BLACK));
		board.setPiece(58, new Bishop(Piece.Colour.BLACK));
		board.setPiece(59, new Queen(Piece.Colour.BLACK));
		board.setPiece(60, new King(Piece.Colour.BLACK));
		board.setPiece(61, new Bishop(Piece.Colour.BLACK));
		board.setPiece(62, new Knight(Piece.Colour.BLACK));
		board.setPiece(63, new Rook(Piece.Colour.BLACK));

		for (int i = 48; i <= 55; i++)
		{
			board.setPiece(i, new Pawn(Piece.Colour.BLACK));
		}

		for (int i = 8; i <= 15; i++)
		{
			board.setPiece(i, new Pawn(Piece.Colour.WHITE));
		}

		board.setPiece(0, new Rook(Piece.Colour.WHITE));
		board.setPiece(1, new Knight(Piece.Colour.WHITE));
		board.setPiece(2, new Bishop(Piece.Colour.WHITE));
		board.setPiece(3, new Queen(Piece.Colour.WHITE));
		board.setPiece(4, new King(Piece.Colour.WHITE));
		board.setPiece(5, new Bishop(Piece.Colour.WHITE));
		board.setPiece(6, new Knight(Piece.Colour.WHITE));
		board.setPiece(7, new Rook(Piece.Colour.WHITE));

		System.out.println(board.toFEN());
	}
}
