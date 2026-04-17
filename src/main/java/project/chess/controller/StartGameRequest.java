package project.chess.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartGameRequest
{
    private String gameMode;
    private String userColour;
    private String initialFEN;
}
