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

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    // Test 1 : Inscription OK
    @Test
    void testRegisterOK() {
        assertDoesNotThrow(() -> authService.register("test@example.com", "abcd"));
    }

    // Test 2 : Inscription refusée si email déjà existant
    @Test
    void testRegisterEmailDejaExistant() {
        authService.register("double@example.com", "abcd");
        assertThrows(ResourceConflictException.class, () ->
                authService.register("double@example.com", "abcd"));
    }

    // Test 3 : Mot de passe trop court
    @Test
    void testRegisterMotDePasseTropCourt() {
        assertThrows(InvalidInputException.class, () ->
                authService.register("test@example.com", "ab"));
    }

    // Test 4 : Login OK
    @Test
    void testLoginOK() {
        authService.register("login@example.com", "abcd");
        assertDoesNotThrow(() -> authService.login("login@example.com", "abcd"));
    }

    // Test 5 : Login KO si mot de passe incorrect
    @Test
    void testLoginMauvaisMotDePasse() {
        authService.register("login2@example.com", "abcd");
        assertThrows(AuthenticationFailedException.class, () ->
                authService.login("login2@example.com", "mauvais"));
    }

    // Test 6 : Login KO si email inconnu
    @Test
    void testLoginEmailInconnu() {
        assertThrows(AuthenticationFailedException.class, () ->
                authService.login("inconnu@example.com", "abcd"));
    }

    // Test 7 : Validation email vide
    @Test
    void testRegisterEmailVide() {
        assertThrows(Exception.class, () ->
                authService.register("", "abcd"));
    }

    // Test 8 : Accès /api/me refusé sans token valide
    @Test
    void testGetUserByTokenInvalide() {
        assertThrows(AuthenticationFailedException.class, () ->
                authService.getUserByToken("tokeninvalide"));
    }
}