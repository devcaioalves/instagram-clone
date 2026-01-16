package br.edu.ifpb.instagram.service.impl;

import br.edu.ifpb.instagram.model.request.LoginRequest;
import br.edu.ifpb.instagram.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldAuthenticateAndReturnJwtToken() {
        LoginRequest request = new LoginRequest("caio", "123456");

        Authentication authentication =
                new UsernamePasswordAuthenticationToken("caio", "123456");

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);

        when(jwtUtils.generateToken(authentication))
                .thenReturn("token-fake");

        String token = authService.authenticate(request);

        assertNotNull(token);
        assertEquals("token-fake", token);
    }
}
