package project.chess.service;

import org.springframework.stereotype.Service;
import project.chess.controller.RecentGameDTO;
import project.chess.controller.StatisticsDTO;
import project.chess.persistence.entity.GameEntity;
import project.chess.persistence.repository.EvaluationRepository;
import project.chess.persistence.repository.GameRepository;
import project.chess.persistence.repository.MoveRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService
{
    private final GameRepository gameRepository;
    private final MoveRepository moveRepository;
    private final EvaluationRepository evaluationRepository;

    public StatisticsService(GameRepository gameRepository,
                             MoveRepository moveRepository,
                             EvaluationRepository evaluationRepository)
    {
        this.gameRepository = gameRepository;
        this.moveRepository = moveRepository;
        this.evaluationRepository = evaluationRepository;
    }

    public StatisticsDTO getStatistics()
    {
        StatisticsDTO statistics = new StatisticsDTO();

        statistics.setTotalGamesPlayed(
                gameRepository.countGames()
        );

        statistics.setTotalCompletedGames(
                gameRepository.countCompletedGames()
        );

        statistics.setTotalMovesAnalysed(
                moveRepository.countMoves()
        );

        Double averageMoveQuality =
                evaluationRepository.getGlobalAverageEvaluationScore();

        if (averageMoveQuality != null)
        {
            statistics.setAverageMoveQuality(
                    BigDecimal.valueOf(averageMoveQuality)
                            .setScale(2, RoundingMode.HALF_UP)
            );
        }

        statistics.setAverageMoveTimeMs(
                moveRepository.getAverageMoveTimeMs()
        );

        statistics.setMostCommonEvaluationLabel(
                evaluationRepository.getMostCommonEvaluationLabel()
        );

        return statistics;
    }

    public List<RecentGameDTO> getRecentGames()
    {
        List<GameEntity> recentGames =
                gameRepository.findRecentGames();

        List<RecentGameDTO> recentGameDTOs =
                new ArrayList<>();

        for (GameEntity game : recentGames)
        {
            RecentGameDTO dto = new RecentGameDTO();

            dto.setGameId(game.getGameId());
            dto.setResult(game.getResult());
            dto.setGameMode(game.getGameMode());
            dto.setUserColour(game.getUserColour());
            dto.setCompleted(game.getCompleted());
            dto.setAverageMoveQuality(game.getAverageMoveQuality());
            dto.setStartedAt(game.getStartedAt());
            dto.setEndedAt(game.getEndedAt());

            recentGameDTOs.add(dto);
        }

        return recentGameDTOs;
    }
}