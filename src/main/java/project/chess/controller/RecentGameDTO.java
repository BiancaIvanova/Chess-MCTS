package project.chess.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RecentGameDTO
{
    private Integer gameId;

    private String result;
    private String gameMode;
    private String userColour;

    private Boolean completed;

    private BigDecimal averageMoveQuality;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}