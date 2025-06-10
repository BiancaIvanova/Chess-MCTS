package project.chess;

import project.chess.datastructures.*;
import project.chess.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Game
{
    private Chessboard board;
    private Piece.Colour currentTurn;
    private List<String> moveHistorySAN;
    private int halfMoveClock;
    private int fullMoveNumber;

    private GameResult result;
    private boolean gameOver;

    public Game()
    {
        board = new Chessboard();
        currentTurn = Piece.Colour.WHITE;
        moveHistorySAN = new ArrayList<>();
        halfMoveClock = 0;
        fullMoveNumber = 1;
        result = GameResult.ONGOING;
        gameOver = false;
    }

    public Chessboard getBoard() { return board; }

    public Piece.Colour getCurrentTurn() { return currentTurn; }

    /**
     * Attempts to make a legal move, with SAN as input.
     * Returns true if the move was legal and made, false otherwise.
     */
    public boolean makeMove(String sanMove)
    {
        if (gameOver) return false;

        // Generate legal moves for current player
        List<Pair<String, Chessboard>> legalMoves = board.generateAllLegalMoveBoards(currentTurn);

        for (Pair<String, Chessboard> move : legalMoves)
        {
            String legalSAN = move.getKey();

            if (legalSAN.equals(sanMove))
            {
                board = move.getValue();
                moveHistorySAN.add(sanMove);

                updateHalfMoveClock(sanMove);
                if (currentTurn == Piece.Colour.BLACK)
                {
                    fullMoveNumber++;
                }

                updateGameStatus();
                switchTurn();

                return true;
            }
        }

        return false; // Illegal move
    }

    private void updateHalfMoveClock(String sanMove)
    {
        boolean isPawnMove = sanMove.matches("^[a-h].*") || sanMove.startsWith("P") || sanMove.matches(".*=.*");
        boolean isCapture = sanMove.contains("x");

        if (isPawnMove || isCapture)
        {
            halfMoveClock = 0;
        }
        else
        {
            halfMoveClock++;
        }
    }

    private void updateGameStatus()
    {
        if (board.isCheckmate(currentTurn))
        {
            gameOver = true;
            result = (currentTurn == Piece.Colour.WHITE) ? GameResult.BLACK_WIN : GameResult.WHITE_WIN;
        }
        else if (board.isStalemate(currentTurn))
        {
            gameOver = true;
            result = GameResult.DRAW;
        }
        else if (halfMoveClock >= 100)
        {
            gameOver = true;
            result = GameResult.DRAW;
        }
    }

    private void switchTurn()
    {
        currentTurn = (currentTurn == Piece.Colour.WHITE) ? Piece.Colour.BLACK : Piece.Colour.WHITE;
    }

    public List<String> getMoveHistory() { return  new ArrayList<>(moveHistorySAN); }

    public GameResult getResult() { return result; }

    public boolean isGameOver() { return gameOver; }

    /**
     * Resets the game to the starting position.
     */
    public void reset()
    {
        board = new Chessboard();
        currentTurn = Piece.Colour.WHITE;
        moveHistorySAN.clear();
        halfMoveClock = 0;
        fullMoveNumber = 1;
        result = GameResult.ONGOING;
        gameOver = false;
    }

    /**
     * Returns a complete FEN string of the current position
     */
    public String getFEN()
    {
        return board.toBasicFEN();
    }
}
