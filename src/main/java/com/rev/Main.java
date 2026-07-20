package com.rev;

import com.rev.api.MovieAPI;
import com.rev.repository.jooq.JooqConfig;
import com.rev.repository.jooq.JooqMovieRepository;

import static spark.Spark.*;

public class Main {
    static void main() {
        port(4000);

        var movieRepository = new JooqMovieRepository(JooqConfig.dslContext());
        var movieAPI = new MovieAPI(movieRepository);

        path("/v1/movies", () -> {
            get("", movieAPI::listMovies,  new JsonTransformer());
            get("/:id", movieAPI::showMovie,  new JsonTransformer());
            post("",       movieAPI::createMovie,  new JsonTransformer());
            put("/:id",     movieAPI::editMovie, new JsonTransformer());
            delete("/:id",  movieAPI::deleteMovie);
        });
    }
}
