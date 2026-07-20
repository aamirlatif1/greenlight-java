package com.rev.repository;

import com.rev.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<User> update(User user);
}
