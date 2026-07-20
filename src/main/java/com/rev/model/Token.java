package com.rev.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Base64;

public record Token(
        String plaintext,
        byte[] hash,
        long userId,
        OffsetDateTime expiry,
        String scope
) {
    public static final String SCOPE_AUTHENTICATION = "authentication";

    private static final SecureRandom RANDOM = new SecureRandom();

    public static Token generate(long userId, Duration ttl, String scope) {
        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        String plaintext = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return new Token(plaintext, sha256(plaintext), userId, OffsetDateTime.now().plus(ttl), scope);
    }

    private static byte[] sha256(String plaintext) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(plaintext.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
