package com.example.auth.controller;

import com.example.auth.entity.User;
import com.example.auth.service.AuthService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST gérant les endpoints d'authentification.
 * <p>
 * Cette implémentation est volontairement dangereuse et ne doit jamais
 * être utilisée en production.
 * </p>
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructeur avec injection du service d'authentification.
     *
     * @param authService le service principal d'authentification
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Inscrit un nouvel utilisateur.
     *
     * @param email    l'adresse email de l'utilisateur
     * @param password le mot de passe en clair
     * @return message de confirmation
     */
    @PostMapping("/auth/register")
    public String register(@RequestParam String email,
                           @RequestParam String password) {
        authService.register(email, password);
        return "User registered";
    }

    /**
     * Authentifie un utilisateur et retourne un token.
     *
     * @param email    l'adresse email de l'utilisateur
     * @param password le mot de passe en clair
     * @return message de succès avec le token généré
     */
    @PostMapping("/auth/login")
    public String login(@RequestParam String email,
                        @RequestParam String password) {
        String token = authService.login(email, password);
        return "Login success. Token: " + token;
    }

    /**
     * Retourne les informations de l'utilisateur authentifié.
     *
     * @param token le token d'authentification
     * @return message de bienvenue avec l'email de l'utilisateur
     */
    @GetMapping("/me")
    public String me(@RequestParam String token) {
        User user = authService.getUserByToken(token);
        return "Bienvenue " + user.getEmail();
    }
}