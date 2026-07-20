package com.rev;

import com.rev.api.MovieAPI;

import static spark.Spark.*;

public class Main {
    static void main() {
        port(4000);


        path("/v1/movies", () -> {
            get("", MovieAPI::listMovies,  new JsonTransformer());
            get("/:id", MovieAPI::showMovie,  new JsonTransformer());
            post("",       MovieAPI::createMovie,  new JsonTransformer());
            put("/:id",     MovieAPI::editMovie, new JsonTransformer());
            delete("/:id",  MovieAPI::deleteMovie);
        });
    }
}
