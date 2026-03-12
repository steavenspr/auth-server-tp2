package com.example.auth.service;

import com.example.auth.entity.User;
import com.example.auth.exception.AuthenticationFailedException;
import com.example.auth.exception.InvalidInputException;
import com.example.auth.exception.ResourceConflictException;
import com.example.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Service principal gérant la logique d'authentification.
 * <p>
 * Cette implémentation est volontairement dangereuse et ne doit jamais
 * être utilisée en production.
 * </p>
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;

    /**
     * Constructeur avec injection du repository.
     *
     * @param userRepository le repository d'accès aux données utilisateur
     */
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Inscrit un nouvel utilisateur avec email et mot de passe.
     *
     * @param email    l'adresse email de l'utilisateur
     * @param password le mot de passe en clair
     * @return l'utilisateur créé et sauvegardé
     * @throws InvalidInputException      si l'email ou le mot de passe est invalide
     * @throws ResourceConflictException  si l'email existe déjà
     */
    public User register(String email, String password) {

        if (email == null || email.isEmpty()) {
            logger.warn("Inscription échouée : email vide");
            throw new InvalidInputException("Email cannot be empty");
        }

        if (!email.contains("@")) {
            logger.warn("Inscription échouée : format email invalide pour {}", email);
            throw new InvalidInputException("Invalid email format");
        }

        if (password.length() < 4) {
            logger.warn("Inscription échouée : mot de passe trop court pour {}", email);
            throw new InvalidInputException("Password too short");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            logger.warn("Inscription échouée : email déjà existant pour {}", email);
            throw new ResourceConflictException("Email already exists");
        }

        User user = new User(email, password);
        userRepository.save(user);
        logger.info("Inscription réussie pour : {}", email);
        return user;
    }

    /**
     * Authentifie un utilisateur et retourne un token.
     *
     * @param email    l'adresse email de l'utilisateur
     * @param password le mot de passe en clair
     * @return le token d'authentification généré
     * @throws AuthenticationFailedException si l'email ou le mot de passe est incorrect
     */
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Connexion échouée : email inconnu {}", email);
                    return new AuthenticationFailedException("Email not found");
                });

        if (!user.getPassword().equals(password)) {
            logger.warn("Connexion échouée : mauvais mot de passe pour {}", email);
            throw new AuthenticationFailedException("Invalid password");
        }

        String token = UUID.randomUUID().toString();
        user.setToken(token);
        userRepository.save(user);
        logger.info("Connexion réussie pour : {}", email);
        return token;
    }

    /**
     * Récupère un utilisateur par son token d'authentification.
     *
     * @param token le token d'authentification
     * @return l'utilisateur correspondant au token
     * @throws AuthenticationFailedException si le token est invalide
     */
    public User getUserByToken(String token) {
        return userRepository.findByToken(token)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid token"));
    }
}