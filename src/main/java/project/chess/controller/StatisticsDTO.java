package project.chess.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class StatisticsDTO
{
    private Integer totalGamesPlayed;
    private Integer totalCompletedGames;
    private Integer totalMovesAnalysed;

    private BigDecimal averageMoveQuality;
    private Integer averageMoveTimeMs;

    private String mostCommonEvaluationLabel;
}