package com.github.hdghg.rbmonitoring.repository;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.TimeZone;

@Repository
public class ActivityRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void persistActivity(String type, String author, String channel, String text) {
        FastDateFormat format = FastDateFormat.getInstance("yy-MM-dd HH:mm:ss", TimeZone.getTimeZone("Europe/Moscow"));
        Timestamp now = Timestamp.from(Instant.now());

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("at", now, Types.TIMESTAMP_WITH_TIMEZONE);
        params.addValue("atMsk", format.format(now), Types.TIMESTAMP_WITH_TIMEZONE);
        params.addValue("type", type, Types.BOOLEAN);
        params.addValue("author", author, Types.VARCHAR);
        params.addValue("channel", channel, Types.VARCHAR);
        params.addValue("text", text, Types.VARCHAR);
        jdbcTemplate.update("insert into activity(at, at_msk, type, author, channel, text) " +
                "values (:at, :atMsk, :type, :author, :channel, :text)", params);
    }

}
