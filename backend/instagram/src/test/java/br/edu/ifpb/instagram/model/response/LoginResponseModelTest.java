package br.edu.ifpb.instagram.model.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseModelTest {

    @Test
    void shouldCreateLoginResponseModelCorrectly() {
        LoginResponseModel response = new LoginResponseModel(
                "caio",
                "token-jwt-123"
        );

        assertNotNull(response);
        assertEquals("caio", response.getUsername());
        assertEquals("token-jwt-123", response.getToken());
    }

    @Test
    void shouldUpdateFieldsUsingSetters() {
        LoginResponseModel response = new LoginResponseModel(
                "user",
                "token"
        );

        response.setUsername("novoUser");
        response.setToken("novoToken");

        assertEquals("novoUser", response.getUsername());
        assertEquals("novoToken", response.getToken());
    }
}
