package com.github.hdghg.rbmonitoring.repository;

import com.github.hdghg.rbmonitoring.model.CharacterBonus;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;

@Repository
public class BonusRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public Pair<List<CharacterBonus>, List<CharacterBonus>> bonusStatus(String party) {
        String sqlLast5 = "select c.nickname, max(bl.at) as taken_at from character c\n" +
                "left join bonus_log bl on c.character_id = bl.character_id\n" +
                "where party " + (party == null ? "is null" : "= :party") + "\n" +
                "group by c.nickname\n" +
                "order by taken_at desc nulls last\n" +
                "limit 5";
        String sqlNext20 = "select c.nickname, max(bl.at) as taken_at from character c\n" +
                "left join bonus_log bl on c.character_id = bl.character_id\n" +
                "where party " + (party == null ? "is null" : "= :party") + "\n" +
                "group by c.nickname\n" +
                "order by taken_at asc nulls first\n" +
                "limit 20\n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("party", params, Types.VARCHAR);
        List<CharacterBonus> last5 = jdbcTemplate.query(sqlLast5, params, new BeanPropertyRowMapper<>(CharacterBonus.class));
        List<CharacterBonus> next20 = jdbcTemplate.query(sqlNext20, params, new BeanPropertyRowMapper<>(CharacterBonus.class));
        return Pair.of(last5, next20);
    }

}
