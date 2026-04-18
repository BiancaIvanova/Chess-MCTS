package project.chess.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AnalysisEvaluationDTO
{
    private String analysedColour;
    private String boardFEN;
    private List<String> rankedMoves;
    private String bestMove;

    public AnalysisEvaluationDTO(String analysedColour,
                                 String boardFEN,
                                 List<String> rankedMoves)
    {
        this.analysedColour = analysedColour;
        this.boardFEN = boardFEN;
        this.rankedMoves = rankedMoves;
        this.bestMove = rankedMoves.isEmpty()
                ? null
                : rankedMoves.get(0);
    }
}