package br.edu.ifpb.instagram.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setup() {
        jwtUtils = new JwtUtils();
    }

    // Geração de token
    @Test
    void deveGerarTokenComUsernameValido() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("teste@gmail.com");

        String token = jwtUtils.generateToken(authentication);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    // Validação de token válido
    @Test
    void deveValidarTokenValido() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("teste@gmail.com");

        String token = jwtUtils.generateToken(authentication);

        boolean valid = jwtUtils.validateToken(token);

        assertTrue(valid);
    }

    // Token inválido / malformado
    @Test
    void deveRetornarFalsoParaTokenInvalido() {
        String tokenInvalido = "token.qualquer.invalido";

        boolean valid = jwtUtils.validateToken(tokenInvalido);

        assertFalse(valid);
    }

    // Extração de username
    @Test
    void deveExtrairUsernameDoToken() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("teste@gmail.com");

        String token = jwtUtils.generateToken(authentication);
        String usernameExtraido = jwtUtils.getUsernameFromToken(token);

        assertEquals("teste@gmail.com", usernameExtraido);
    }

    // Erro ao extrair username de token inválido
    @Test
    void deveLancarExcecaoAoExtrairUsernameDeTokenInvalido() {
        String tokenInvalido = "token.invalido";

        assertThrows(Exception.class, () ->
                jwtUtils.getUsernameFromToken(tokenInvalido)
        );
    }
}
