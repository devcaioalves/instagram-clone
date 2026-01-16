package br.edu.ifpb.instagram.controller;

import br.edu.ifpb.instagram.exception.FieldAlreadyExistsException;
import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.security.JwtAuthenticationFilter;
import br.edu.ifpb.instagram.security.JwtUtils;
import br.edu.ifpb.instagram.service.UserService;
import br.edu.ifpb.instagram.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtAuthenticationFilter.class
                )
})
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtils  jwtUtils;

    @MockitoBean
    private AuthServiceImpl  authService;

    @MockitoBean
    private UserService  userService;

    // metodo que testa o fluxo de login com credenciais válidas.
    @Test
    void deveRetornar200AoRealizarLoginComSucesso() throws Exception {

        String jsonRequest = """
            {
                "username": "teste@gmail.com",
                "password": "123456"
            }
            """;

        when(authService.authenticate(any()))
                .thenReturn("token-fake-123");
        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("teste@gmail.com"))
                .andExpect(jsonPath("$.token").value("token-fake-123"));
    }

    // metodo que testa o fluxo de login com credenciais inválidas
    @Test
    void deveRetornar401QuandoLoginForInvalido() throws Exception {

        String jsonRequest = """
            {
                "username": "teste@gmail.com",
                "password": "senhaErrada"
            }
            """;

        when(authService.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas."));
        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(status().isUnauthorized());
    }

    // metodo que testa o cadastro de um usuário com dados válidos
    @Test
    void deveRetornar201QuandoCadastroForValido() throws Exception {

        String jsonRequest = """
        {
          "fullName": "Teste da Silva",
          "username": "teste",
          "email": "teste@gmail.com",
          "password": "123456"
        }
        """;

        UserDto userDto = new UserDto(
                1L,
                "Teste da Silva",
                "teste",
                "teste@gmail.com",
                null,
                null);
        when(userService.createUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("teste"))
                .andExpect(jsonPath("$.email").value("teste@gmail.com"));
    }

    //metodo que testa o cadastro quando o e-mail já existe
    @Test
    void deveRetonar409QuandoEmailJaExistir() throws Exception {

        String jsonRequest = """
        {
          "fullName": "Teste da Silva",
          "username": "teste",
          "email": "teste@gmail.com",
          "password": "123456"
        }
        """;

        when(userService.createUser(any()))
                .thenThrow(new FieldAlreadyExistsException("Email já existente."));
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }
}
