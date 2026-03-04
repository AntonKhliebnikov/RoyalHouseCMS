package com.royalhouse.cms.security.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RememberMeTokenDao {
    private final JdbcTemplate jdbcTemplate;

    public RememberMeTokenDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteByUsername(String username) {
        jdbcTemplate.update("delete from persistent_logins where username = ?", username);
    }
}