package com.example.auth;

import com.example.auth.exception.AuthenticationFailedException;
import com.example.auth.exception.InvalidInputException;
import com.example.auth.exception.ResourceConflictException;
import com.example.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import com.example.auth.exception.AccountLockedException;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    // Mot de passe valide selon la nouvelle politique TP2
    private static final String VALID_PASSWORD = "Motdepasse1!";

    @Test
    void testRegisterOK() {
        assertDoesNotThrow(() -> authService.register("test@example.com", VALID_PASSWORD));
    }

    @Test
    void testRegisterEmailDejaExistant() {
        authService.register("double@example.com", VALID_PASSWORD);
        assertThrows(ResourceConflictException.class, () ->
                authService.register("double@example.com", VALID_PASSWORD));
    }

    @Test
    void testRegisterMotDePasseTropCourt() {
        assertThrows(InvalidInputException.class, () ->
                authService.register("test@example.com", "ab"));
    }

    @Test
    void testRegisterMotDePasseSansMajuscule() {
        assertThrows(InvalidInputException.class, () ->
                authService.register("test@example.com", "motdepasse1!aa"));
    }

    @Test
    void testRegisterMotDePasseSansSpecial() {
        assertThrows(InvalidInputException.class, () ->
                authService.register("test@example.com", "Motdepasse123"));
    }

    @Test
    void testLoginOK() {
        authService.register("login@example.com", VALID_PASSWORD);
        assertDoesNotThrow(() -> authService.login("login@example.com", VALID_PASSWORD));
    }

    @Test
    void testLoginMauvaisMotDePasse() {
        authService.register("login2@example.com", VALID_PASSWORD);
        assertThrows(AuthenticationFailedException.class, () ->
                authService.login("login2@example.com", "MauvaisMotDePasse1!"));
    }

    @Test
    void testLoginEmailInconnu() {
        assertThrows(AuthenticationFailedException.class, () ->
                authService.login("inconnu@example.com", VALID_PASSWORD));
    }

    @Test
    void testRegisterEmailVide() {
        assertThrows(Exception.class, () ->
                authService.register("", VALID_PASSWORD));
    }

    @Test
    void testGetUserByTokenInvalide() {
        assertThrows(AuthenticationFailedException.class, () ->
                authService.getUserByToken("tokeninvalide"));
    }

    @Test
    void testLockoutApres5Echecs() {
        authService.register("lock@example.com", VALID_PASSWORD);
        // 5 tentatives échouées
        for (int i = 0; i < 5; i++) {
            try {
                authService.login("lock@example.com", "MauvaisMotDePasse1!");
            } catch (AuthenticationFailedException | AccountLockedException e) {
                // attendu
            }
        }
        // La 6ème tentative doit retourner AccountLockedException
        assertThrows(AccountLockedException.class, () ->
                authService.login("lock@example.com", VALID_PASSWORD));
    }

    @Test
    void testNonDivulgationErreur() {
        authService.register("test2@example.com", VALID_PASSWORD);
        // Email inconnu
        AuthenticationFailedException ex1 = assertThrows(
                AuthenticationFailedException.class, () ->
                        authService.login("inconnu2@example.com", VALID_PASSWORD));
        // Mauvais mot de passe
        AuthenticationFailedException ex2 = assertThrows(
                AuthenticationFailedException.class, () ->
                        authService.login("test2@example.com", "MauvaisMotDePasse1!"));
        // Les deux messages doivent être identiques
        assertEquals(ex1.getMessage(), ex2.getMessage());
    }
}