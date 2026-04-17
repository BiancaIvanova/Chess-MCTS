package project.chess.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.chess.persistence.entity.EvaluationEntity;
import project.chess.service.GameService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class ChessController
{
    private final GameService gameService;

    public ChessController(GameService gameService)
    {
        this.gameService = gameService;
    }

    @GetMapping("/state")
    public BoardStateDTO getBoardState(HttpSession session)
    {
        return gameService.getBoardState(session);
    }

    @PostMapping("/game/start")
    public BoardStateDTO startGame(@RequestBody(required = false) StartGameRequest request, HttpSession session)
    {
        return gameService.startGame(request, session);
    }

    @PostMapping("/move")
    public BoardStateDTO makeMove(@RequestParam(required = false) String move,
                                  @RequestBody(required = false) MakeMoveRequest request,
                                  HttpSession session)
    {
        if (request == null)
        {
            request = new MakeMoveRequest();
        }

        if (move != null)
        {
            request.setMove(move);
        }

        return gameService.makeMove(request, session);
    }

    @PostMapping("/games/{gameId}/moves")
    public BoardStateDTO makeMoveForGame(@PathVariable Integer gameId,
                                         @RequestBody MakeMoveRequest request,
                                         HttpSession session)
    {
        request.setGameId(gameId);
        return gameService.makeMove(request, session);
    }

    @DeleteMapping("/games/{gameId}")
    public void deleteGame(@PathVariable Integer gameId)
    {
        gameService.deleteGame(gameId);
    }

    @GetMapping("/evaluation/{moveId}")
    public ResponseEntity<EvaluationEntity> getEvaluation(@PathVariable Integer moveId)
    {
        EvaluationEntity evaluation = gameService.getEvaluationForMove(moveId);

        if (evaluation == null)
        {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(evaluation);
    }
}
