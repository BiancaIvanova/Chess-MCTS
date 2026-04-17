package project.chess.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MoveEntity
{
    private Integer moveId;
    private Integer gameId;
    private Integer moveNumber;
    private String sideToMove;
    private String moveSan;
    private Integer timeTakenMs;
    private String fenBefore;
    private String fenAfter;
    private LocalDateTime createdAt;
}