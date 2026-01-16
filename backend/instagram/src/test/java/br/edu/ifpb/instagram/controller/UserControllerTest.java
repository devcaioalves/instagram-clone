package br.edu.ifpb.instagram.controller;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.request.UserDetailsRequest;
import br.edu.ifpb.instagram.security.JwtAuthenticationFilter;
import br.edu.ifpb.instagram.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtAuthenticationFilter.class
                )
        })
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // ================= GET /users =================
    @Test
    void deveRetornarListaDeUsuarios() throws Exception {

        List<UserDto> users = List.of(
                new UserDto(1L, "User 1", "user1", "u1@email.com", null, null),
                new UserDto(2L, "User 2", "user2", "u2@email.com", null, null)
        );

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].username").value("user1"));
    }

    // ================= GET /users/{id} =================
    @Test
    void deveRetornarUsuarioPorId() throws Exception {

        UserDto user = new UserDto(
                1L, "User 1", "user1", "u1@email.com", null, null
        );

        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("u1@email.com"));
    }

    // ================= PUT /users =================
    @Test
    void deveAtualizarUsuarioComSucesso() throws Exception {

        UserDetailsRequest request = new UserDetailsRequest(
                1L,
                "Novo Nome",
                "novoUser",
                "novo@email.com",
                "123456"
        );

        UserDto updated = new UserDto(
                1L,
                "Novo Nome",
                "novoUser",
                "novo@email.com",
                null,
                null
        );

        when(userService.updateUser(any())).thenReturn(updated);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Novo Nome"))
                .andExpect(jsonPath("$.username").value("novoUser"));
    }

    // ================= DELETE /users/{id} =================
    @Test
    void deveDeletarUsuarioComSucesso() throws Exception {

        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("user was deleted!"));

        verify(userService).deleteUser(1L);
    }
}
