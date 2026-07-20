package com.rev.api;

import com.google.gson.Gson;
import com.rev.model.Movie;
import com.rev.repository.MovieRepository;
import spark.Request;
import spark.Response;

import java.util.List;

import static spark.Spark.halt;

public class MovieAPI {

    private final Gson gson = new Gson();
    private final MovieRepository repository;

    public MovieAPI(MovieRepository repository) {
        this.repository = repository;
    }

    public List<Movie> listMovies(Request request, Response response) {
        response.type("application/json");
        return repository.findAll();
    }

    public Movie showMovie(Request request, Response response) {
        long movieID = Long.parseLong(request.params(":id"));
        var movie = repository.findById(movieID);
        if (movie.isEmpty()) {
            halt(404, "movie not found");
        }
        response.type("application/json");
        return movie.get();
    }

    public Movie createMovie(Request request, Response response) {
        var input = gson.fromJson(request.body(), MovieInput.class);
        var movie = repository.save(new Movie(0, input.title(), input.year(), input.runtime(), input.genres(), 0));
        response.type("application/json");
        response.status(201);
        return movie;
    }

    public Movie editMovie(Request request, Response response) {
        long movieID = Long.parseLong(request.params(":id"));
        var input = gson.fromJson(request.body(), EditMovieInput.class);
        var updated = repository.update(
                new Movie(movieID, input.title(), input.year(), input.runtime(), input.genres(), input.version()));
        if (updated.isEmpty()) {
            halt(409, "movie was modified by another request");
        }
        response.type("application/json");
        return updated.get();
    }

    public String deleteMovie(Request request, Response response) {
        long movieID = Long.parseLong(request.params(":id"));
        if (!repository.deleteById(movieID)) {
            halt(404, "movie not found");
        }
        response.status(200);
        return "";
    }

    private record MovieInput(String title, int year, String runtime, List<String> genres) {}

    private record EditMovieInput(String title, int year, String runtime, List<String> genres, int version) {}
}
