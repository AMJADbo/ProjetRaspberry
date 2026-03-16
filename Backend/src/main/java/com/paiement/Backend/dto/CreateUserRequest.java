package com.paiement.Backend.dto;

import com.paiement.Backend.entity.User;

public class CreateUserRequest {
    private String nom;
    private String email;
    private String motDePasse;
    private User.Role role;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }
}