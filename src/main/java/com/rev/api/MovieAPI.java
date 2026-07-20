package com.rev.api;

import com.rev.model.Movie;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class MovieAPI {

    public static List<Movie> listMovies(Request request, Response response) {
        var movie = new Movie(1, "Moana", 2016, "107 min", List.of("animation", "adventure"), 1);
        var movies = List.of(movie);
        response.type("application/json");
        return movies;
    }

    public static Movie showMovie(Request request, Response response) {
        int movieID = Integer.parseInt(request.params(":id"));
        var movie = new Movie(movieID, "Moana", 2016, "107 min", List.of("animation", "adventure"), 1);
        response.type("application/json");
        return movie;
    }

    public static Movie createMovie(Request request, Response response) {
        var movie = new Movie(1, "Moana", 2016, "107 min", List.of("animation", "adventure"), 1);
        response.type("application/json");
        response.status(201);
        return movie;
    }

    public static Movie editMovie(Request request, Response response) {
        int movieID = Integer.parseInt(request.params(":id"));
        var movie = new Movie(movieID, "Moana", 2016, "107 min", List.of("animation", "adventure"), 1);
        response.type("application/json");
        return movie;
    }

    public static Object deleteMovie(Request request, Response response) {
        var movie = new Movie(1, "Moana", 2016, "107 min", List.of("animation", "adventure"), 1);
        response.status(200);
        return null;
    }
}
