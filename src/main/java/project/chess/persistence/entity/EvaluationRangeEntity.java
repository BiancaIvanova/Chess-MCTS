package project.chess.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class EvaluationRangeEntity
{
    private Integer rangeId;
    private BigDecimal minScore;
    private BigDecimal maxScore;
    private String evalName;
    private String description;
}