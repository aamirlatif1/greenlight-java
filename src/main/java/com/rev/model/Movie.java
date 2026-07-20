package com.rev.model;

import java.util.List;

public record Movie(
        long id,
        String title,
        int year,
        String runtime,
        List<String> genres,
        int version
) {}
