package br.edu.ifpb.instagram.security.jwt;

import br.edu.ifpb.instagram.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;

    //roda antes de cada teste
    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
    }

    //metodo que gera o token
    @Test
    void deveGerarTokenComUsernameValido() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("teste@gmail.com");

        String token = jwtUtils.generateToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    //metodo que valida o token
    @Test
    void deveValidarTokenValido() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("teste@gmail.com");

        String token = jwtUtils.generateToken(authentication);

        boolean valid = jwtUtils.validateToken(token);

        assertTrue(valid);
    }

    //metodo que deve retornar falso para token invalido
    @Test
    void deveRetornarFalsoParaTokenInvalido() {
        Authentication authentication = mock(Authentication.class);

        String tokenInvalido = "token.qualquer.invalido";

        boolean valid = jwtUtils.validateToken(tokenInvalido);

        assertFalse(valid);
    }

    //metodo que extrai o username do token
    @Test
    void deveExtrairUsernameDoToken(){
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("teste@gmail.com");

        String token = jwtUtils.generateToken(authentication);
        String userNameExtraido =  jwtUtils.getUsernameFromToken(token);

        assertEquals("teste@gmail.com", userNameExtraido);
    }
}
