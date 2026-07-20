package com.rev.model;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;

public final class Password {

    private Password() {}

    public static byte[] hash(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt()).getBytes(StandardCharsets.UTF_8);
    }

    public static boolean matches(String plaintext, byte[] hash) {
        return BCrypt.checkpw(plaintext, new String(hash, StandardCharsets.UTF_8));
    }
}
