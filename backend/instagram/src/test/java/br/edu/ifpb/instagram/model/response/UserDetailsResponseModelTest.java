package br.edu.ifpb.instagram.model.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsResponseModelTest {

    @Test
    void shouldSetAndGetAllFields() {
        UserDetailsResponseModel response = new UserDetailsResponseModel();

        response.setId(1L);
        response.setFullName("Caio Alves");
        response.setUsername("caio");
        response.setEmail("teste@email.com");

        assertEquals(1L, response.getId());
        assertEquals("Caio Alves", response.getFullName());
        assertEquals("caio", response.getUsername());
        assertEquals("teste@email.com", response.getEmail());
    }
}
