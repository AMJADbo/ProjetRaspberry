package com.paiement.Backend.Controller;

import com.paiement.Backend.dto.LoginRequest;
import com.paiement.Backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
    this.authService = authService;
}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.login(request)
            .map(user -> ResponseEntity.ok().body(user))
            .orElse(ResponseEntity.status(401).build());
    }
}