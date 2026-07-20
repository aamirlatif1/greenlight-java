package com.rev.repository;

import com.rev.model.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {

    List<Movie> findAll();

    Optional<Movie> findById(long id);

    Movie save(Movie movie);

    Optional<Movie> update(Movie movie);

    boolean deleteById(long id);
}
