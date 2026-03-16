package com.paiement.Backend.service;

import com.paiement.Backend.dto.CreateUserRequest;
import com.paiement.Backend.entity.User;
import com.paiement.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(CreateUserRequest request) {
        User user = new User();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        user.setMotDePasse(request.getMotDePasse());
        user.setRole(request.getRole());
        return userRepository.save(user);
    }

    public User toggleActif(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        user.setActif(!user.isActif());
        return userRepository.save(user);
    }
}