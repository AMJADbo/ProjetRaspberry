package com.paiement.Backend.Controller;

import com.paiement.Backend.dto.LoginRequest;
import com.paiement.Backend.service.AuthService;
import com.paiement.Backend.entity.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.paiement.Backend.repository.BadgeRepository;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final BadgeRepository badgeRepository;

   public AuthController(AuthService authService, BadgeRepository badgeRepository) {
    this.authService = authService;
    this.badgeRepository = badgeRepository;
}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.login(request)
            .map(user -> ResponseEntity.ok().body(user))
            .orElse(ResponseEntity.status(401).build());
    }

@PostMapping("/badge")
public ResponseEntity<?> loginByBadge(@RequestBody Map<String, String> body,
                                       HttpSession session) {
    String uid = body.get("uidRfid");
    return badgeRepository.findByUidRfidAndActifTrue(uid)
        .map(badge -> {
            session.setAttribute("sessionUser", badge.getUser());
            User user = badge.getUser();
            Map<String, String> response = new java.util.HashMap<>();
            response.put("role", user.getRole().name());
            response.put("nom", user.getNom());
            return ResponseEntity.ok().body((Object) response);
        })
        .orElse(ResponseEntity.status(401).build());
}

}