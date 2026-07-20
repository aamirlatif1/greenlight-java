package com.rev.repository.jooq;

import com.rev.model.Token;
import com.rev.repository.TokenRepository;
import org.jooq.DSLContext;

import java.time.Duration;

import static com.rev.jooq.Tables.TOKENS;

public class JooqTokenRepository implements TokenRepository {

    private final DSLContext dsl;

    public JooqTokenRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Token create(long userId, Duration ttl, String scope) {
        Token token = Token.generate(userId, ttl, scope);
        dsl.insertInto(TOKENS, TOKENS.HASH, TOKENS.USER_ID, TOKENS.EXPIRY, TOKENS.SCOPE)
                .values(token.hash(), token.userId(), token.expiry(), token.scope())
                .execute();
        return token;
    }
}
