package com.rev;

import com.rev.api.ExceptionHandlers;
import com.rev.api.MovieAPI;
import com.rev.api.UserAPI;
import com.rev.repository.jooq.JooqConfig;
import com.rev.repository.jooq.JooqMovieRepository;
import com.rev.repository.jooq.JooqTokenRepository;
import com.rev.repository.jooq.JooqUserRepository;

import static spark.Spark.*;

public class Main {
    static void main() {
        port(4000);

        var dsl = JooqConfig.dslContext();

        var movieRepository = new JooqMovieRepository(dsl);
        var movieAPI = new MovieAPI(movieRepository);

        var userRepository = new JooqUserRepository(dsl);
        var tokenRepository = new JooqTokenRepository(dsl);
        var userAPI = new UserAPI(userRepository, tokenRepository);

        path("/v1/movies", () -> {
            get("", movieAPI::listMovies,  new JsonTransformer());
            get("/:id", movieAPI::showMovie,  new JsonTransformer());
            post("",       movieAPI::createMovie,  new JsonTransformer());
            put("/:id",     movieAPI::editMovie, new JsonTransformer());
            delete("/:id",  movieAPI::deleteMovie);
        });

        path("/v1/users", () -> {
            post("",      userAPI::registerUser, new JsonTransformer());
            post("/login", userAPI::loginUser,   new JsonTransformer());
        });

        ExceptionHandlers.register();
    }
}
