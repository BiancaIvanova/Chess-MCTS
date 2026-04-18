package project.chess.persistence.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import project.chess.persistence.entity.EvaluationEntity;

import java.util.List;

@Repository
public class EvaluationRepository
{
    private final JdbcTemplate jdbcTemplate;

    public EvaluationRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(EvaluationEntity evaluation)
    {
        String sql = """
                INSERT INTO evaluations
                (move_id, eval_range_id, evaluation_score, best_move_san,
                 evaluation_time_ms, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                evaluation.getMoveId(),
                evaluation.getRangeId(),
                evaluation.getEvaluationScore(),
                evaluation.getBestMoveSan(),
                evaluation.getEvaluationTimeMs(),
                evaluation.getCreatedAt()
        );
    }

    public Double getAverageEvaluationScore(Integer gameId)
    {
        String sql = """
                SELECT AVG(e.evaluation_score)
                FROM evaluations e
                INNER JOIN moves m ON e.move_id = m.move_id
                WHERE m.game_id = ?
                """;

        return jdbcTemplate.queryForObject(sql, Double.class, gameId);
    }

    public EvaluationEntity findByMoveId(Integer moveId)
    {
        String sql = """
            SELECT evaluation_id, move_id, eval_range_id, evaluation_score,
                   best_move_san, evaluation_time_ms, created_at
            FROM evaluations
            WHERE move_id = ?
            LIMIT 1
            """;

        var results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            EvaluationEntity evaluation = new EvaluationEntity();

            evaluation.setEvaluationId(rs.getInt("evaluation_id"));
            evaluation.setMoveId(rs.getInt("move_id"));
            evaluation.setRangeId(rs.getInt("eval_range_id"));
            evaluation.setEvaluationScore(rs.getBigDecimal("evaluation_score"));
            evaluation.setBestMoveSan(rs.getString("best_move_san"));
            evaluation.setEvaluationTimeMs(rs.getInt("evaluation_time_ms"));
            evaluation.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            return evaluation;
        }, moveId);

        return results.isEmpty() ? null : results.get(0);
    }

    public Double getGlobalAverageEvaluationScore()
    {
        String sql = """
            SELECT AVG(evaluation_score)
            FROM evaluations
            """;

        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    public String getMostCommonEvaluationLabel()
    {
        String sql = """
            SELECT er.eval_name
            FROM evaluations e
            JOIN evaluation_ranges er
                ON e.eval_range_id = er.eval_range_id
            GROUP BY er.eval_name
            ORDER BY COUNT(*) DESC
            LIMIT 1
            """;

        List<String> results = jdbcTemplate.query(
                sql,
                (resultSet, rowNum) ->
                        resultSet.getString("eval_name")
        );

        if (results.isEmpty())
        {
            return null;
        }

        return results.get(0);
    }
}