package project.chess.model;

import lombok.Getter;
import project.chess.datastructure.*;
import project.chess.piece.Piece;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a chess game.
 * Handles move validation, manages turns, and handles FEN import/export.
 *
 * @see Chessboard
 * @see GameResult
 */

public class Game
{
    private Chessboard board;
    @Getter
    private Piece.Colour currentTurn;
    private final List<String> moveHistorySAN;
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

    /**
     * Copy constructor
     */
    public Game(Game other)
    {
        board = new Chessboard(other.board);
        currentTurn = other.currentTurn;
        moveHistorySAN = new ArrayList<>(other.moveHistorySAN);
        halfMoveClock = other.halfMoveClock;
        fullMoveNumber = other.fullMoveNumber;
        result = other.result;
        gameOver = other.gameOver;
    }

    public Chessboard getBoard() { return board; }

    /**
     * Attempts to make a legal move in SAN notation.
     * @param sanMove The move in SAN notation.
     * @return true If the move was legal and made, false otherwise.
     */
    public boolean makeValidMove(String sanMove)
    {
        if (gameOver) return false;

        // Generate legal moves for the current player
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

    /**
     * Makes a move without validation. Assumes the move is valid.
     * @param move A {@link Pair} containing SAN notation and resulting {@link Chessboard}.
     */
    public void makeMove(Pair<String, Chessboard> move)
    {
        String sanMove = move.getKey();

        board = move.getValue();
        moveHistorySAN.add(sanMove);

        updateHalfMoveClock(sanMove);
        if (currentTurn == Piece.Colour.BLACK)
        {
            fullMoveNumber++;
        }

        updateGameStatus();
        switchTurn();
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
        StringBuilder fen = new StringBuilder();

        fen.append(board.toBasicFEN());
        fen.append(' ');

        fen.append(currentTurn == Piece.Colour.WHITE ? 'w' : 'b');
        fen.append(' ');

        fen.append(castlingRightsToFEN(board.getCastlingRights()));
        fen.append(' ');

        fen.append(enPassantTargetToFEN(board.getEnPassantTarget()));
        fen.append(' ');

        fen.append(halfMoveClock);
        fen.append(' ');

        fen.append(fullMoveNumber);

        return fen.toString();
    }

    /**
     * Imports a game position from FEN notation.
     * @param fen The FEN string to parse.
     * @throws IllegalArgumentException If FEN format is invalid.
     */
    public void importFEN(String fen)
    {
        String[] parts = fen.trim().split("\\s+");
        if (parts.length != 6)
            throw new IllegalArgumentException("Invalid FEN string: must have 6 fields");

        board.importBasicFEN(parts[0]);

        // Parse active colour
        if (parts[1].equalsIgnoreCase("w"))
        {
            currentTurn = Piece.Colour.WHITE;
        }
        else if (parts[1].equalsIgnoreCase("b"))
        {
            currentTurn = Piece.Colour.BLACK;
        }
        else
            throw new IllegalArgumentException("Invalid colour format: must have either w or b");

        board.setCastlingRights(parseCastlingRights(parts[2]));

        board.setEnPassantTarget(parseEnPassantTargetFromFEN(parts[3]));

        try {
            halfMoveClock = Integer.parseInt(parts[4]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format: half move number must be an integer");
        }

        try {
            fullMoveNumber = Integer.parseInt(parts[5]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format: full move number must be an integer");
        }

        moveHistorySAN.clear();
        gameOver = false;
        result = GameResult.ONGOING;
    }

    private String castlingRightsToFEN(Set<CastlingRight> castlingRights)
    {
        if (castlingRights.isEmpty()) return "-";

        StringBuilder sb = new StringBuilder();
        if (castlingRights.contains(CastlingRight.WHITE_KINGSIDE)) sb.append('K');
        if (castlingRights.contains(CastlingRight.WHITE_QUEENSIDE)) sb.append('Q');
        if (castlingRights.contains(CastlingRight.BLACK_KINGSIDE)) sb.append('k');
        if (castlingRights.contains(CastlingRight.BLACK_QUEENSIDE)) sb.append('q');

        return sb.toString();
    }

    private Set<CastlingRight> parseCastlingRights(String castlingRightsFEN)
    {
        Set<CastlingRight> castlingRights = new HashSet<>();
        if (castlingRightsFEN.equals("-")) return castlingRights;

        for (char c : castlingRightsFEN.toCharArray())
        {
            switch (c)
            {
                case 'K': castlingRights.add(CastlingRight.WHITE_KINGSIDE); break;
                case 'Q': castlingRights.add(CastlingRight.WHITE_QUEENSIDE); break;
                case 'k': castlingRights.add(CastlingRight.BLACK_KINGSIDE); break;
                case 'q': castlingRights.add(CastlingRight.BLACK_QUEENSIDE); break;
                default: break;
            }
        }

        return castlingRights;
    }

    private String enPassantTargetToFEN(int enPassantTarget)
    {
        if (enPassantTarget < 0 || enPassantTarget >= Chessboard.BOARD_SIZE) return "-";
        return BoardUtils.toCoordinate(enPassantTarget);
    }

    private int parseEnPassantTargetFromFEN(String fenSquare)
    {
        if (fenSquare.equals("-")) return -1;
        return BoardUtils.toIndex(fenSquare);
    }

    public Piece.Colour getWinner()
    {
        if (this.getResult() == GameResult.WHITE_WIN) return Piece.Colour.WHITE;
        if (this.getResult() == GameResult.BLACK_WIN) return Piece.Colour.BLACK;
        return null;
    }
}
