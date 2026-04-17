package project.chess.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MakeMoveRequest
{
    private Integer gameId;
    private String move;
    private Integer timeTakenMs;
}
