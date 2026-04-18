package project.chess.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.chess.service.AnalysisService;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AnalysisController
{
    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService)
    {
        this.analysisService = analysisService;
    }

    @GetMapping("/state")
    public BoardStateDTO getAnalysisState(HttpSession session)
    {
        return analysisService.getAnalysisState(session);
    }

    @PostMapping("/import-fen")
    public BoardStateDTO importFEN(@RequestBody FenRequest request,
                                   HttpSession session)
    {
        return analysisService.importFEN(request, session);
    }

    @PostMapping("/move")
    public BoardStateDTO makeAnalysisMove(@RequestBody MakeMoveRequest request,
                                          HttpSession session)
    {
        return analysisService.makeAnalysisMove(request, session);
    }

    @PostMapping("/reset")
    public BoardStateDTO resetAnalysisBoard(HttpSession session)
    {
        return analysisService.resetAnalysisBoard(session);
    }

    @PostMapping("/evaluate")
    public void evaluatePosition(@RequestParam String colour,
                                 HttpSession session)
    {
        analysisService.evaluatePositionAsync(colour, session);
    }

    @GetMapping("/evaluation")
    public ResponseEntity<AnalysisEvaluationDTO> getEvaluation(HttpSession session)
    {
        AnalysisEvaluationDTO evaluation = analysisService.getEvaluation(session);

        if (evaluation == null)
        {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(evaluation);
    }
}