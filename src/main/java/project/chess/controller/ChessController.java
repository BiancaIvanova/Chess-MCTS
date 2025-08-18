package project.chess.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import project.chess.model.Game;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class ChessController
{
    @GetMapping("/state")
    public BoardStateDTO getBoardState(HttpSession session)
    {
        // Try to get game from session
        Game game = (Game) session.getAttribute("game");

        if (game == null)
        {
            // First visit to the site: create and store a new game
            game = new Game();
            game.importFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            session.setAttribute("game", game);
        }

        return new BoardStateDTO(game);
    }

    @PostMapping("/move")
    public BoardStateDTO makeMove(@RequestParam String move, HttpSession session)
    {
        Game game = (Game) session.getAttribute("game");
        if (game == null)
        {
            game = new Game();
            session.setAttribute("game", game);
        }
        game.makeValidMove(move);
        return new BoardStateDTO(game);
    }
}
