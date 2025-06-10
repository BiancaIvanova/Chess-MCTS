package project.chess.controllers;

import org.springframework.web.bind.annotation.*;
import project.chess.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class ChessController
{
    private final Chessboard board = new Chessboard();

    @GetMapping("/board")
    public String getBoardFEN()
    {
        return board.toBasicFEN();
    }
}
