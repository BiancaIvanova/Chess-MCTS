package project.chess.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EvaluationEntity
{
    private Integer evaluationId;
    private Integer moveId;
    private Integer rangeId;
    private BigDecimal evaluationScore;
    private String bestMoveSan;
    private Integer evaluationTimeMs;
    private LocalDateTime createdAt;
}