package br.edu.ifpb.instagram.security;

import br.edu.ifpb.instagram.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtUtils jwtUtils;
    private UserDetailsServiceImpl userDetailsService;
    private JwtAuthenticationFilter filter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setup() {
        jwtUtils = mock(JwtUtils.class);
        userDetailsService = mock(UserDetailsServiceImpl.class);
        filter = new JwtAuthenticationFilter(jwtUtils, userDetailsService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    // 1️⃣ Sem header Authorization
    @Test
    void deveIgnorarQuandoNaoHouverAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // 2️⃣ Header inválido (não começa com Bearer)
    @Test
    void deveIgnorarQuandoAuthorizationNaoForBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // 3️⃣ Token inválido
    @Test
    void naoDeveAutenticarQuandoTokenForInvalido() throws Exception {
        when(request.getHeader("Authorization"))
                .thenReturn("Bearer token-invalido");

        when(jwtUtils.getUsernameFromToken("token-invalido"))
                .thenReturn("user");

        when(jwtUtils.validateToken("token-invalido"))
                .thenReturn(false);

        UserDetails user = new User(
                "user", "123", Collections.emptyList()
        );

        when(userDetailsService.loadUserByUsername("user"))
                .thenReturn(user);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // 4️⃣ Token válido → autentica
    @Test
    void deveAutenticarQuandoTokenForValido() throws Exception {
        when(request.getHeader("Authorization"))
                .thenReturn("Bearer token-valido");

        when(jwtUtils.getUsernameFromToken("token-valido"))
                .thenReturn("user");

        when(jwtUtils.validateToken("token-valido"))
                .thenReturn(true);

        UserDetails user = new User(
                "user", "123", Collections.emptyList()
        );

        when(userDetailsService.loadUserByUsername("user"))
                .thenReturn(user);

        filter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();

        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("user");
    }
}
