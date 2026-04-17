package project.chess.persistence.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import project.chess.persistence.entity.EvaluationRangeEntity;

import java.math.BigDecimal;

@Repository
public class EvaluationRangeRepository
{
    private final JdbcTemplate jdbcTemplate;

    public EvaluationRangeRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(EvaluationRangeEntity range)
    {
        String sql = """
                INSERT INTO evaluation_ranges
                (min_score, max_score, eval_name, description)
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                range.getMinScore(),
                range.getMaxScore(),
                range.getEvalName(),
                range.getDescription()
        );
    }

    public int count()
    {
        String sql = """
                SELECT COUNT(*)
                FROM evaluation_ranges
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);

        return count == null ? 0 : count;
    }

    public EvaluationRangeEntity findRangeForScore(BigDecimal score)
    {
        String sql = """
                SELECT eval_range_id, min_score, max_score, eval_name, description
                FROM evaluation_ranges
                WHERE ? >= min_score AND ? <= max_score
                LIMIT 1
                """;

        var results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            EvaluationRangeEntity range = new EvaluationRangeEntity();

            range.setRangeId(rs.getInt("eval_range_id"));
            range.setMinScore(rs.getBigDecimal("min_score"));
            range.setMaxScore(rs.getBigDecimal("max_score"));
            range.setEvalName(rs.getString("eval_name"));
            range.setDescription(rs.getString("description"));

            return range;
        }, score, score);

        return results.isEmpty() ? null : results.get(0);
    }
}