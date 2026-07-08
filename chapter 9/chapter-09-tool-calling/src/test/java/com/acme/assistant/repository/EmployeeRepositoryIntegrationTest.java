package com.acme.assistant.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
class EmployeeRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16")
            .withDatabaseName("acme_ai_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @Test
    void shouldPersistAndQueryEmployeeTable() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             Statement statement = connection.createStatement()) {

            statement.execute("create extension if not exists vector");
            statement.execute("create table if not exists employees (id varchar(64) primary key, full_name varchar(255))");
            statement.execute("insert into employees (id, full_name) values ('EMP-9001', 'Integration User')");

            try (ResultSet resultSet = statement.executeQuery("select count(*) from employees where id = 'EMP-9001'")) {
                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getInt(1)).isEqualTo(1);
            }
        }
    }
}
