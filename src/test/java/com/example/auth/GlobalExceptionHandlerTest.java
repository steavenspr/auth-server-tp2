package com.example.auth;

import com.example.auth.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Tests d'intégration du GlobalExceptionHandler via les endpoints HTTP.
 * Vérifie que les exceptions métier sont correctement converties en réponses JSON.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRegisterEmailInvalideRetourne400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .param("email", "emailinvalide")
                        .param("password", "Motdepasse1!"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testRegisterEmailDejaExistantRetourne409() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .param("email", "conflit@example.com")
                        .param("password", "Motdepasse1!"));

        mockMvc.perform(post("/api/auth/register")
                        .param("email", "conflit@example.com")
                        .param("password", "Motdepasse1!"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void testLoginEmailInconnuRetourne401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .param("email", "inconnu@example.com")
                        .param("password", "Motdepasse1!"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void testLoginCompteBloqueRetourne423() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .param("email", "bloque@example.com")
                        .param("password", "Motdepasse1!"));

        for (int i = 0; i < 6; i++) {
            mockMvc.perform(post("/api/auth/login")
                    .param("email", "bloque@example.com")
                    .param("password", "MauvaisMotDePasse1!"));
        }

        mockMvc.perform(post("/api/auth/login")
                        .param("email", "bloque@example.com")
                        .param("password", "Motdepasse1!"))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.status").value(423));
    }

    @Test
    void testMeTokenInvalideRetourne401() throws Exception {
        mockMvc.perform(get("/api/me")
                        .param("token", "tokeninvalide"))
                .andExpect(status().isUnauthorized());
    }
}