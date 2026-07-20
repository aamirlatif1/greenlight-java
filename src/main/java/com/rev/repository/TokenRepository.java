package com.rev.repository;

import com.rev.model.Token;

import java.time.Duration;

public interface TokenRepository {

    Token create(long userId, Duration ttl, String scope);
}
