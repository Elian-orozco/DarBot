package com.darbot.auth.controller;

import com.darbot.auth.dto.LoginRequest;
import com.darbot.auth.dto.LoginResponse;
import com.darbot.auth.dto.RegisterRequest;
import com.darbot.auth.dto.UserResponse;
import com.darbot.usuarios.entity.Usuario;
import com.darbot.auth.security.UserPrincipal;
import com.darbot.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.authenticate(loginRequest);

        Usuario usuario = authService.findByUsername(loginRequest.getUsername());

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .username(usuario.getUsername())
                .email(usuario.getCorreo())
                .rol(usuario.getRoles().stream().findFirst().map(r -> r.getNombre()).orElse(null))
                .userId(usuario.getId())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Usuario usuario = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(usuario));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Usuario usuario = authService.findByUsername(userPrincipal.getUsername());
        return ResponseEntity.ok(UserResponse.from(usuario));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}
