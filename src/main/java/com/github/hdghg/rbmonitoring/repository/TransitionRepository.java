package com.github.hdghg.rbmonitoring.repository;

import com.github.hdghg.rbmonitoring.model.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;

@Repository
public class TransitionRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void insert(Transition transition) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", transition.getName(), Types.VARCHAR);
        params.addValue("toStatus", transition.isAlive(), Types.BOOLEAN);
        params.addValue("at", transition.getAt(), Types.TIMESTAMP_WITH_TIMEZONE);
        jdbcTemplate.update("insert into transition(name, alive, at) values (:name, :toStatus, :at)",
                params);
    }

    public List<Transition> listAll() {
        return jdbcTemplate.query("select * from transition", new BeanPropertyRowMapper<>(Transition.class));
    }
}