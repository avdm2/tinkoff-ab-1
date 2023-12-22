package ru.tinkoff.hse.security.services.interfaces;

import ru.tinkoff.hse.security.dto.JwtAuthenticationResponse;
import ru.tinkoff.hse.security.dto.SignInRequest;
import ru.tinkoff.hse.security.dto.SignUpRequest;

public interface AuthenticationService {

    JwtAuthenticationResponse signUp(SignUpRequest request);
    JwtAuthenticationResponse signIn(SignInRequest request);
}
