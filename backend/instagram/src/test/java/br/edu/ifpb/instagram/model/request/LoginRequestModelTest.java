package br.edu.ifpb.instagram.model.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestModelTest {

    @Test
    void shouldSetAndGetUsernameAndPassword() {
        LoginRequestModel request = new LoginRequestModel();

        request.setUsername("caio");
        request.setPassword("123456");

        assertEquals("caio", request.getUsername());
        assertEquals("123456", request.getPassword());
    }
}
