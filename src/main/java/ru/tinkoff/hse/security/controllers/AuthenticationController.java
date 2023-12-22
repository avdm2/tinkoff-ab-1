package ru.tinkoff.hse.security.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tinkoff.hse.notes.services.DirectoryService;
import ru.tinkoff.hse.security.dto.JwtAuthenticationResponse;
import ru.tinkoff.hse.security.dto.SignInRequest;
import ru.tinkoff.hse.security.dto.SignUpRequest;
import ru.tinkoff.hse.security.services.interfaces.AuthenticationService;

@RestController
@RequestMapping("/notes/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final DirectoryService directoryService;

    public AuthenticationController(AuthenticationService authenticationService, DirectoryService directoryService) {
        this.authenticationService = authenticationService;
        this.directoryService = directoryService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody SignUpRequest request) {
        ResponseEntity<JwtAuthenticationResponse> response = ResponseEntity.ok(authenticationService.signUp(request));

        directoryService.createRoot(request.getUsername());
        return response;
    }

    @PostMapping("/signIn")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(authenticationService.signIn(request));
    }
}
