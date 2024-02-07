package com.github.hdghg.rbmonitoring.repository;

import com.github.hdghg.rbmonitoring.model.CharacterBonus;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

@Repository
public class BonusRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public Pair<List<CharacterBonus>, List<CharacterBonus>> bonusStatus(String party) {
        String sqlLast5 = "select c.character_id as id, c.nickname, max(bl.at) as at from character c\n" +
                "left join bonus_log bl on c.character_id = bl.character_id\n" +
                "where party " + (party == null ? "is null" : "= :party") + "\n" +
                "group by c.character_id, c.nickname\n" +
                "order by at desc nulls last\n" +
                "limit 5";
        String sqlNext20 = "select c.character_id as id, c.nickname, max(bl.at) as at from character c\n" +
                "left join bonus_log bl on c.character_id = bl.character_id\n" +
                "where party " + (party == null ? "is null" : "= :party") + "\n" +
                "group by c.character_id, c.nickname\n" +
                "order by at asc nulls first\n" +
                "limit 20\n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("party", party, Types.VARCHAR);
        List<CharacterBonus> last5 = jdbcTemplate.query(sqlLast5, params, new BeanPropertyRowMapper<>(CharacterBonus.class));
        Collections.reverse(last5);
        List<CharacterBonus> next20 = jdbcTemplate.query(sqlNext20, params, new BeanPropertyRowMapper<>(CharacterBonus.class));
        return Pair.of(last5, next20);
    }

    public boolean registerBonusTaken(Integer id) {
        FastDateFormat format = FastDateFormat.getInstance("yy-MM-dd HH:mm:ss", TimeZone.getTimeZone("Europe/Moscow"));
        Timestamp now = Timestamp.from(Instant.now());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("at", now, Types.TIMESTAMP_WITH_TIMEZONE);
        params.addValue("atMsk", format.format(now), Types.VARCHAR);
        params.addValue("user", "todo", Types.VARCHAR);
        params.addValue("characterId", id, Types.INTEGER);
        String sql = "insert into bonus_log (at, at_msk, user, character_id) " +
                "values (:at, :atMsk, :user, :characterId)";
        return jdbcTemplate.update(sql, params) > 0;
    }

}
