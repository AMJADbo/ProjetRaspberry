package com.paiement.Backend.service;

import com.paiement.Backend.dto.LoginRequest;
import com.paiement.Backend.entity.User;
import com.paiement.Backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(LoginRequest request) {
        return userRepository
            .findByEmailAndMotDePasseAndActifTrue(
                request.getEmail(),
                request.getMotDePasse()
            );
    }
}