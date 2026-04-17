package project.chess.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GameEntity
{
    private Integer gameId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String result;
    private String gameMode;
    private String userColour;
    private Boolean completed;
    private Integer totalUserTimeMs;
    private BigDecimal averageMoveQuality;
    private Integer averageMoveTimeMs;
}