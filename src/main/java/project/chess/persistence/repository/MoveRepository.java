package project.chess.persistence.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import project.chess.persistence.entity.MoveEntity;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class MoveRepository
{
    private final JdbcTemplate jdbcTemplate;

    public MoveRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer save(MoveEntity move)
    {
        String sql = """
                INSERT INTO moves
                (game_id, move_number, side_to_move, move_san, time_taken_ms,
                 fen_before, fen_after, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, move.getGameId());
            statement.setInt(2, move.getMoveNumber());
            statement.setString(3, move.getSideToMove());
            statement.setString(4, move.getMoveSan());
            statement.setInt(5, move.getTimeTakenMs());
            statement.setString(6, move.getFenBefore());
            statement.setString(7, move.getFenAfter());
            statement.setObject(8, move.getCreatedAt());

            return statement;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public List<MoveEntity> findByGameIdOrderByMoveNumberAsc(Integer gameId)
    {
        String sql = """
                SELECT move_id, game_id, move_number, side_to_move, move_san,
                       time_taken_ms, fen_before, fen_after, created_at
                FROM moves
                WHERE game_id = ?
                ORDER BY move_number ASC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            MoveEntity move = new MoveEntity();

            move.setMoveId(rs.getInt("move_id"));
            move.setGameId(rs.getInt("game_id"));
            move.setMoveNumber(rs.getInt("move_number"));
            move.setSideToMove(rs.getString("side_to_move"));
            move.setMoveSan(rs.getString("move_san"));
            move.setTimeTakenMs(rs.getInt("time_taken_ms"));
            move.setFenBefore(rs.getString("fen_before"));
            move.setFenAfter(rs.getString("fen_after"));
            move.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            return move;
        }, gameId);
    }

    public Integer countMoves()
    {
        String sql = """
            SELECT COUNT(*)
            FROM moves
            """;

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer getAverageMoveTimeMs()
    {
        String sql = """
            SELECT AVG(time_taken_ms)
            FROM moves
            """;

        Double result = jdbcTemplate.queryForObject(sql, Double.class);

        if (result == null)
        {
            return null;
        }

        return result.intValue();
    }
}