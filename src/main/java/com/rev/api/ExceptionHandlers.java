package com.rev.api;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Response;

import static spark.Spark.exception;

public class ExceptionHandlers {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlers.class);
    private static final Gson gson = new Gson();

    private ExceptionHandlers() {}

    public static void register() {
        exception(ApiException.class, (exception, request, response) ->
            respondWithError(response, exception.status(), exception.getMessage())
        );

        exception(NumberFormatException.class, (exception, request, response) ->
                respondWithError(response, 400, "invalid id parameter"));

        exception(Exception.class, (exception, request, response) -> {
            log.error("unhandled exception", exception);
            respondWithError(response, 500, "the server encountered a problem and could not process your request");
        });
    }

    private static void respondWithError(Response response, int status, String message) {
        response.status(status);
        response.type("application/json");
        response.body(gson.toJson(new ErrorResponse(message)));
    }

    private record ErrorResponse(String error) {}
}
