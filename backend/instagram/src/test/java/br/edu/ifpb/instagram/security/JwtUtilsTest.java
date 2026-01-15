package br.edu.ifpb.instagram.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setup() {
        jwtUtils = new JwtUtils();
    }

    @Test
    void shouldGenerateValidJwtToken() {
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("caio");

        String token = jwtUtils.generateToken(authentication);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldValidateValidToken() {
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("caio");

        String token = jwtUtils.generateToken(authentication);

        boolean isValid = jwtUtils.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void shouldInvalidateMalformedToken() {
        String invalidToken = "token.invalido.qualquer";

        boolean isValid = jwtUtils.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void shouldGetUsernameFromToken() {
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("caio");

        String token = jwtUtils.generateToken(authentication);

        String username = jwtUtils.getUsernameFromToken(token);

        assertEquals("caio", username);
    }

    @Test
    void shouldFailWhenGettingUsernameFromInvalidToken() {
        String invalidToken = "token.invalido";

        assertThrows(Exception.class, () ->
                jwtUtils.getUsernameFromToken(invalidToken)
        );
    }
}
