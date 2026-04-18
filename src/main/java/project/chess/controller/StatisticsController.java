package project.chess.controller;

import org.springframework.web.bind.annotation.*;
import project.chess.service.StatisticsService;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class StatisticsController
{
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService)
    {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public StatisticsDTO getStatistics()
    {
        return statisticsService.getStatistics();
    }

    @GetMapping("/recent-games")
    public List<RecentGameDTO> getRecentGames()
    {
        return statisticsService.getRecentGames();
    }
}