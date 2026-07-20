package com.rev.repository.jooq;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

public final class JooqConfig {

    private JooqConfig() {
    }

    public static DSLContext dslContext() {
        var url = System.getenv().getOrDefault("DATABASE_URL", "jdbc:postgresql://localhost:5432/greenlight");
        var user = System.getenv().getOrDefault("DATABASE_USER", "databaseUser");
        var password = System.getenv().getOrDefault("DATABASE_PASSWORD", "databasePassword");
        return DSL.using(url, user, password);
    }
}
