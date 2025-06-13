package project.chess.controllers;

import lombok.Getter;
import lombok.Setter;
import project.chess.Game;

import java.util.List;
import java.util.Map;

/*
DTO = Data Transfer Object
 */

@Getter
@Setter
public class BoardStateDTO
{
    private String turn;
    private Map<String, List<String>> allLegalMoves;
    private boolean check;
    private String lastMove;
    private String boardFEN;

    public BoardStateDTO(Game game)
    {
        this.turn = game.getCurrentTurn().toString();
        this.allLegalMoves = game.getBoard().getAllPieceMovesAsMap();
        this.check = game.getBoard().isInCheck(game.getCurrentTurn());
        var moves = game.getMoveHistory();
        this.lastMove = moves.isEmpty() ? null : moves.getLast();
        this.boardFEN = game.getFEN();
    }
}
