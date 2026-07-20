package com.rev.model;

import java.time.OffsetDateTime;

public record User(
        long id,
        OffsetDateTime createdAt,
        String name,
        String email,
        byte[] passwordHash,
        boolean activated,
        int version
) {}
