package org.hwmoodle.testutil;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hwmoodle.config.HibernateUtil;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

@Testcontainers
public abstract class IntegrationTestBase {
    @Container
    protected static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void initDatabaseProperties() {
        System.setProperty("test.db.url", POSTGRES.getJdbcUrl());
        System.setProperty("test.db.username", POSTGRES.getUsername());
        System.setProperty("test.db.password", POSTGRES.getPassword());
    }

    @BeforeEach
    void cleanDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("delete from User").executeUpdate();
            transaction.commit();
        }
    }

    @AfterAll
    static void shutdown() {
        HibernateUtil.shutdown();
    }
}

