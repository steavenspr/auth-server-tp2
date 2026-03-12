package com.example.auth.exception;

/**
 * Exception lancée quand un compte est temporairement bloqué
 * suite à trop de tentatives de connexion échouées.
 */
public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) {
        super(message);
    }
}