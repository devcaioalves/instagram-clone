package br.edu.ifpb.instagram.model.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsRequestModelTest {

    @Test
    void shouldSetAndGetAllFields() {
        UserDetailsRequestModel request = new UserDetailsRequestModel();

        request.setId(1L);
        request.setEmail("teste@email.com");
        request.setPassword("123456");
        request.setFullName("Caio Alves");
        request.setUsername("caio");

        assertEquals(1L, request.getId());
        assertEquals("teste@email.com", request.getEmail());
        assertEquals("123456", request.getPassword());
        assertEquals("Caio Alves", request.getFullName());
        assertEquals("caio", request.getUsername());
    }

    @Test
    void shouldStartWithNullId() {
        UserDetailsRequestModel request = new UserDetailsRequestModel();

        assertNull(request.getId());
    }
}
