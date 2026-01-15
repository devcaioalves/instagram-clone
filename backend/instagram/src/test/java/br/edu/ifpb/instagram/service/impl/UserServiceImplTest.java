package br.edu.ifpb.instagram.service.impl;

import br.edu.ifpb.instagram.exception.FieldAlreadyExistsException;
import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    // FIND BY ID

    @Test
    void shouldReturnUserDtoWhenUserExists() {
        Long userId = 1L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setFullName("Paulo Pereira");
        userEntity.setEmail("paulo@ppereira.dev");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));

        UserDto userDto = userService.findById(userId);

        assertNotNull(userDto);
        assertEquals(userId, userDto.id());
        assertEquals("Paulo Pereira", userDto.fullName());
        assertEquals("paulo@ppereira.dev", userDto.email());

        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.findById(99L)
        );

        assertTrue(ex.getMessage().contains("User not found"));
        verify(userRepository).findById(99L);
    }

    // CREATE USER

    @Test
    void shouldCreateUserSuccessfully() {
        UserDto dto = new UserDto(
                null,
                "Caio Alves",
                "caio",
                "caio@email.com",
                "123456",
                null
        );

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userRepository.existsByUsername(dto.username())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("encrypted");

        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> {
                    UserEntity entity = invocation.getArgument(0);
                    entity.setId(1L);
                    return entity;
                });

        UserDto result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals("caio", result.username());
        assertEquals("caio@email.com", result.email());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserDto dto = new UserDto(
                null, "Caio", "caio", "caio@email.com", "123", null
        );

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(FieldAlreadyExistsException.class,
                () -> userService.createUser(dto));

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        UserDto dto = new UserDto(
                null, "Caio", "caio", "caio@email.com", "123", null
        );

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userRepository.existsByUsername(dto.username())).thenReturn(true);

        assertThrows(FieldAlreadyExistsException.class,
                () -> userService.createUser(dto));

        verify(userRepository, never()).save(any());
    }

    // UPDATE USER

    @Test
    void shouldUpdateUserSuccessfullyWithPassword() {
        UserDto dto = new UserDto(
                1L,
                "Novo Nome",
                "novoUser",
                "novo@email.com",
                "novaSenha",
                null
        );

        UserEntity entity = new UserEntity();
        entity.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(passwordEncoder.encode(dto.password())).thenReturn("encrypted");
        when(userRepository.save(any(UserEntity.class))).thenReturn(entity);

        UserDto updated = userService.updateUser(dto);

        assertEquals("novoUser", updated.username());
        assertEquals("novo@email.com", updated.email());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {
        UserDto dto = new UserDto(
                99L, "Nome", "user", "email", null, null
        );

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.updateUser(dto));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithNullId() {
        UserDto dto = new UserDto(
                null, "Nome", "user", "email", null, null
        );

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(dto));
    }

    // DELETE USER

    @Test
    void shouldDeleteUserSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingUser() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> userService.deleteUser(1L));

        verify(userRepository, never()).deleteById(any());
    }
}
