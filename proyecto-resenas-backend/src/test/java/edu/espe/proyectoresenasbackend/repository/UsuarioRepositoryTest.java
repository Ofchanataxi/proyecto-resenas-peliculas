package edu.espe.proyectoresenasbackend.repository;

import edu.espe.proyectoresenasbackend.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests de UsuarioRepository")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Usuario usuario1;
    private Usuario usuario2;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();

        usuario1 = new Usuario();
        usuario1.setNombreCompleto("Juan Pérez");
        usuario1.setEmail("juan@test.com");
        usuario1.setContrasena("password123");
        usuario1.setActivo(true);

        usuario2 = new Usuario();
        usuario2.setNombreCompleto("María García");
        usuario2.setEmail("maria@test.com");
        usuario2.setContrasena("password456");
        usuario2.setActivo(false);
    }

    @Test
    @DisplayName("Debe guardar un usuario correctamente")
    void testSaveUsuario() {
        Usuario savedUsuario = usuarioRepository.save(usuario1);

        assertThat(savedUsuario).isNotNull();
        assertThat(savedUsuario.getId()).isNotNull();
        assertThat(savedUsuario.getEmail()).isEqualTo("juan@test.com");
        assertThat(savedUsuario.getNombreCompleto()).isEqualTo("Juan Pérez");
    }

    @Test
    @DisplayName("Debe encontrar un usuario por email")
    void testFindByEmail() {
        entityManager.persistAndFlush(usuario1);

        Optional<Usuario> foundUsuario = usuarioRepository.findByEmail("juan@test.com");

        assertThat(foundUsuario).isPresent();
        assertThat(foundUsuario.get().getNombreCompleto()).isEqualTo("Juan Pérez");
    }

    @Test
    @DisplayName("Debe retornar empty cuando el email no existe")
    void testFindByEmailNotFound() {
        Optional<Usuario> foundUsuario = usuarioRepository.findByEmail("noexiste@test.com");

        assertThat(foundUsuario).isEmpty();
    }

    @Test
    @DisplayName("Debe verificar si existe un usuario por email")
    void testExistsByEmail() {
        entityManager.persistAndFlush(usuario1);

        boolean exists = usuarioRepository.existsByEmail("juan@test.com");
        boolean notExists = usuarioRepository.existsByEmail("noexiste@test.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Debe contar usuarios activos")
    void testCountByActivoTrue() {
        entityManager.persist(usuario1);
        entityManager.persist(usuario2);
        entityManager.flush();

        Long activeCount = usuarioRepository.countByActivoTrue();

        assertThat(activeCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe contar usuarios inactivos")
    void testCountByActivoFalse() {
        entityManager.persist(usuario1);
        entityManager.persist(usuario2);
        entityManager.flush();

        Long inactiveCount = usuarioRepository.countByActivoFalse();

        assertThat(inactiveCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe listar todos los usuarios")
    void testFindAll() {
        entityManager.persist(usuario1);
        entityManager.persist(usuario2);
        entityManager.flush();

        List<Usuario> usuarios = usuarioRepository.findAll();

        assertThat(usuarios).hasSize(2);
        assertThat(usuarios).extracting(Usuario::getEmail)
                .containsExactlyInAnyOrder("juan@test.com", "maria@test.com");
    }

    @Test
    @DisplayName("Debe actualizar un usuario existente")
    void testUpdateUsuario() {
        Usuario savedUsuario = entityManager.persistAndFlush(usuario1);

        savedUsuario.setNombreCompleto("Juan Carlos Pérez");
        savedUsuario.setActivo(false);
        Usuario updatedUsuario = usuarioRepository.save(savedUsuario);

        assertThat(updatedUsuario.getNombreCompleto()).isEqualTo("Juan Carlos Pérez");
        assertThat(updatedUsuario.getActivo()).isFalse();
    }

    @Test
    @DisplayName("Debe eliminar un usuario por ID")
    void testDeleteById() {
        Usuario savedUsuario = entityManager.persistAndFlush(usuario1);
        Long usuarioId = savedUsuario.getId();

        usuarioRepository.deleteById(usuarioId);

        Optional<Usuario> deletedUsuario = usuarioRepository.findById(usuarioId);
        assertThat(deletedUsuario).isEmpty();
    }

    @Test
    @DisplayName("Debe implementar UserDetails correctamente")
    void testUserDetailsImplementation() {
        Usuario savedUsuario = usuarioRepository.save(usuario1);

        assertThat(savedUsuario.getUsername()).isEqualTo("juan@test.com");
        assertThat(savedUsuario.getPassword()).isEqualTo("password123");
        assertThat(savedUsuario.isEnabled()).isTrue();
        assertThat(savedUsuario.isAccountNonExpired()).isTrue();
        assertThat(savedUsuario.isAccountNonLocked()).isTrue();
        assertThat(savedUsuario.isCredentialsNonExpired()).isTrue();
        assertThat(savedUsuario.getAuthorities()).isNotEmpty();
    }
}