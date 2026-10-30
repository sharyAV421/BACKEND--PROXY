package com.example.aiproxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class H2DatabaseIntegrationTest {
    @Autowired private JdbcTemplate jdbcTemplate;

    @Test
    void startsWithH2AndCreatesJpaTables() {
        String databaseProduct=jdbcTemplate.execute((ConnectionCallback<String>)
                connection -> connection.getMetaData().getDatabaseProductName());
        Integer userTableRows=jdbcTemplate.queryForObject("select count(*) from app_users", Integer.class);
        assertEquals("H2", databaseProduct);
        assertEquals(0, userTableRows);
    }
}
