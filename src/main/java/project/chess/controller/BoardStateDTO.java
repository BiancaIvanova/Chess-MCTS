package project.chess.controller;

import lombok.Getter;
import lombok.Setter;
import project.chess.model.Game;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/*
DTO = Data Transfer Object
 */

@Getter
@Setter
public class BoardStateDTO
{
    private Integer gameId;
    private String turn;
    private Map<String, List<String>> allLegalMoves;
    private boolean check;
    private String lastMove;
    private String boardFEN;
    private String result;
    private boolean gameOver;

    private BigDecimal evaluationScore;
    private String evaluationLabel;
    private String bestMoveSan;

    private Integer moveId;
    private Boolean evaluationPending;

    public BoardStateDTO(Game game)
    {
        this(game, null, null, null, null);
    }

    public BoardStateDTO(Game game, Integer gameId)
    {
        this(game, gameId, null, null, null);
    }

    public BoardStateDTO(Game game, Integer gameId, BigDecimal evaluationScore, String evaluationLabel, String bestMoveSan)
    {
        this.gameId = gameId;
        this.turn = game.getCurrentTurn().toString();
        this.allLegalMoves = game.getBoard().getAllPieceMovesAsMap();
        this.check = game.getBoard().isInCheck(game.getCurrentTurn());

        var moves = game.getMoveHistory();
        this.lastMove = moves.isEmpty() ? null : moves.get(moves.size() - 1);

        this.boardFEN = game.getFEN();
        this.result = game.getResult().toString();
        this.gameOver = game.isGameOver();

        this.evaluationScore = evaluationScore;
        this.evaluationLabel = evaluationLabel;
        this.bestMoveSan = bestMoveSan;

        this.evaluationPending = evaluationScore == null;
    }

    public BoardStateDTO(Game game, Integer gameId, Integer moveId)
    {
        this(game, gameId, null, null, null);
        this.moveId = moveId;
        this.evaluationPending = true;
    }
}