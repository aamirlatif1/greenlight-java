package com.rev.repository;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("a user with this email address already exists");
    }
}
