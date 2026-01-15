package br.edu.ifpb.instagram.repository;

import br.edu.ifpb.instagram.model.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private UserEntity createUser(String username, String email) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName("Teste User");
        user.setEncryptedPassword("123");
        return userRepository.save(user);
    }

    @Test
    void shouldCheckIfEmailExists() {
        createUser("caio", "caio@email.com");

        boolean exists = userRepository.existsByEmail("caio@email.com");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckIfUsernameExists() {
        createUser("caio", "caio@email.com");

        boolean exists = userRepository.existsByUsername("caio");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindByUsername() {
        createUser("caio", "caio@email.com");

        var user = userRepository.findByUsername("caio");

        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo("caio@email.com");
    }

    @Test
    void shouldReturnAllUsers() {
        createUser("u1", "u1@email.com");
        createUser("u2", "u2@email.com");

        var users = userRepository.findAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void shouldUpdateUserPartially() {
        UserEntity user = createUser("oldUser", "old@email.com");

        int updatedRows = userRepository.updatePartialUser(
                "Novo Nome",
                null,
                null,
                "novaSenha",
                user.getId()
        );

        assertThat(updatedRows).isEqualTo(1);

        UserEntity updated = userRepository.findById(user.getId()).get();
        assertThat(updated.getFullName()).isEqualTo("Novo Nome");
        assertThat(updated.getEncryptedPassword()).isEqualTo("novaSenha");
        assertThat(updated.getEmail()).isEqualTo("old@email.com");
    }

    @Test
    void shouldReturnZeroWhenUpdatingNonExistingUser() {
        int updatedRows = userRepository.updatePartialUser(
                "Nome",
                null,
                null,
                null,
                999L
        );

        assertThat(updatedRows).isZero();
    }
}
