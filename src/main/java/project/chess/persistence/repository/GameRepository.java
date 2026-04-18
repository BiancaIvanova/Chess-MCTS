package project.chess.persistence.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import project.chess.persistence.entity.GameEntity;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class GameRepository
{
    private final JdbcTemplate jdbcTemplate;

    public GameRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer save(GameEntity game)
    {
        String sql = """
                INSERT INTO games
                (start_time_utc, end_time_utc, result, game_mode, user_colour, completed,
                 total_user_time_ms, average_move_quality, average_move_time_ms)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setObject(1, game.getStartedAt());
            statement.setObject(2, game.getEndedAt());
            statement.setString(3, game.getResult());
            statement.setString(4, game.getGameMode());
            statement.setString(5, game.getUserColour());
            statement.setBoolean(6, game.getCompleted());
            statement.setInt(7, game.getTotalUserTimeMs());
            statement.setBigDecimal(8, game.getAverageMoveQuality());
            statement.setObject(9, game.getAverageMoveTimeMs());

            return statement;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public boolean existsById(Integer gameId)
    {
        String sql = """
                SELECT COUNT(*)
                FROM games
                WHERE game_id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, gameId);

        return count != null && count > 0;
    }

    public void updateSummary(GameEntity game)
    {
        String sql = """
                UPDATE games
                SET end_time_utc = ?,
                    result = ?,
                    completed = ?,
                    total_user_time_ms = ?,
                    average_move_quality = ?,
                    average_move_time_ms = ?
                WHERE game_id = ?
                """;

        jdbcTemplate.update(
                sql,
                game.getEndedAt(),
                game.getResult(),
                game.getCompleted(),
                game.getTotalUserTimeMs(),
                game.getAverageMoveQuality(),
                game.getAverageMoveTimeMs(),
                game.getGameId()
        );
    }

    public void deleteById(Integer gameId)
    {
        String sql = """
                DELETE FROM games
                WHERE game_id = ?
                """;

        jdbcTemplate.update(sql, gameId);
    }

    public Integer countGames()
    {
        String sql = """
            SELECT COUNT(*)
            FROM games
            """;

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer countCompletedGames()
    {
        String sql = """
            SELECT COUNT(*)
            FROM games
            WHERE completed = TRUE
            """;

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<GameEntity> findRecentGames()
    {
        String sql = """
            SELECT *
            FROM games
            ORDER BY start_time_utc DESC
            LIMIT 10
            """;

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) -> {
                    GameEntity game = new GameEntity();

                    game.setGameId(resultSet.getInt("game_id"));
                    game.setStartedAt(
                            resultSet.getTimestamp("start_time_utc")
                                    .toLocalDateTime()
                    );

                    if (resultSet.getTimestamp("end_time_utc") != null)
                    {
                        game.setEndedAt(
                                resultSet.getTimestamp("end_time_utc")
                                        .toLocalDateTime()
                        );
                    }

                    game.setResult(resultSet.getString("result"));
                    game.setGameMode(resultSet.getString("game_mode"));
                    game.setUserColour(resultSet.getString("user_colour"));
                    game.setCompleted(resultSet.getBoolean("completed"));

                    if (resultSet.getBigDecimal("average_move_quality") != null)
                    {
                        game.setAverageMoveQuality(
                                resultSet.getBigDecimal("average_move_quality")
                        );
                    }

                    if (resultSet.getObject("average_move_time_ms") != null)
                    {
                        game.setAverageMoveTimeMs(
                                resultSet.getInt("average_move_time_ms")
                        );
                    }

                    game.setTotalUserTimeMs(
                            resultSet.getInt("total_user_time_ms")
                    );

                    return game;
                }
        );
    }

    public void abandonOngoingGames(LocalDateTime endedAt)
    {
        String sql = """
            UPDATE games
            SET end_time_utc = ?,
                result = ?,
                completed = ?
            WHERE result = ?
            """;

        jdbcTemplate.update(
                sql,
                endedAt,
                "ABANDONED",
                true,
                "ONGOING"
        );
    }
}