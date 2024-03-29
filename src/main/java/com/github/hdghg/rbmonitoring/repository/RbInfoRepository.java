package com.github.hdghg.rbmonitoring.repository;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class RbInfoRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void insertOrIgnore(Map<String, Integer> values) {
        if (MapUtils.isEmpty(values)) {
            return;
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder sb = new StringBuilder("insert or ignore into rb_info(name, level) values \n");
        int i = 1;
        for (Map.Entry<String, Integer> e : values.entrySet()) {
            sb.append(String.format("(:name%s, :level%s)\n", i, i)).append(",");
            params.addValue("name" + i, e.getKey());
            params.addValue("level" + i, e.getValue());
            i++;
        }
        sb.deleteCharAt(sb.length() - 1);
        jdbcTemplate.update(sb.toString(), params);
    }

    public Map<String, String> correctNames() {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select name, correct_name from rb_info", Collections.emptyMap());
        return maps.stream()
                .collect(Collectors.toMap(m -> m.get("name").toString(), m -> m.get("correct_name").toString()));
    }
}
