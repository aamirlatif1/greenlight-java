package com.rev.api;

import com.rev.model.Password;
import com.rev.model.Token;
import com.rev.model.User;
import com.rev.repository.DuplicateEmailException;
import com.rev.repository.TokenRepository;
import com.rev.repository.UserRepository;
import spark.Request;
import spark.Response;

import java.time.Duration;

public class UserAPI {

    private final UserRepository users;
    private final TokenRepository tokens;

    public UserAPI(UserRepository users, TokenRepository tokens) {
        this.users = users;
        this.tokens = tokens;
    }

    public UserResponse registerUser(Request request, Response response) {
        var input = JsonBodyReader.read(request, RegisterUserInput.class);

        User user;
        try {
            user = users.save(new User(0, null, input.name(), input.email(), Password.hash(input.password()), false, 0));
        } catch (DuplicateEmailException e) {
            throw new BadRequestException(e.getMessage());
        }

        response.type("application/json");
        response.status(201);
        return UserResponse.from(user);
    }

    public AuthenticationTokenResponse loginUser(Request request, Response response) {
        var input = JsonBodyReader.read(request, LoginInput.class);

        var user = users.findByEmail(input.email())
                .filter(u -> Password.matches(input.password(), u.passwordHash()))
                .orElseThrow(() -> new ApiException(401, "invalid authentication credentials"));

        var token = tokens.create(user.id(), Duration.ofHours(24), Token.SCOPE_AUTHENTICATION);

        response.type("application/json");
        response.status(201);
        return new AuthenticationTokenResponse(token.plaintext(), token.expiry().toString());
    }

    private record RegisterUserInput(String name, String email, String password) {}

    private record LoginInput(String email, String password) {}

    private record UserResponse(long id, String name, String email, boolean activated) {
        static UserResponse from(User user) {
            return new UserResponse(user.id(), user.name(), user.email(), user.activated());
        }
    }

    private record AuthenticationTokenResponse(String token, String expiry) {}
}
