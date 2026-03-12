package com.example.auth.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité représentant un utilisateur dans la base de données.
 * <p>
 * Cette implémentation est volontairement dangereuse et ne doit jamais
 * être utilisée en production.
 * </p>
 */
@Entity
@Table(name = "users")
public class User {

    /** Identifiant unique généré automatiquement. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Adresse email unique de l'utilisateur. */
    @Column(unique = true)
    private String email;

    /**
     * Mot de passe stocké en clair.
     * ATTENTION : volontairement dangereux, ne jamais faire en production.
     */
    private String password;

    /** Token d'authentification basique stocké en base. */
    private String token;

    /** Date et heure de création du compte. */
    private LocalDateTime createdAt;

    /** Constructeur vide requis par JPA. */
    public User() {}

    /**
     * Constructeur principal pour créer un utilisateur.
     *
     * @param email    l'adresse email de l'utilisateur
     * @param password le mot de passe en clair (dangereux)
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
// getters & setters
}