package edu.espe.proyectoresenasbackend.repository;

import edu.espe.proyectoresenasbackend.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UsuarioRepositoryTest {
    @Autowired
    private UsuarioRepository repository;

    @Test
    void shouldSaveAndFindUsuarioByEmail() {
        Usuario u = new Usuario();
        u.setNombreCompleto("Test User");
        u.setEmail("test@example.com");
        u.setContrasena("password123"); // Contraseña (en test puede ser simple)
        u.setActivo(true);

        repository.save(u);

        var result = repository.findByEmail("test@example.com");
        assertThat(result).isPresent();
        assertThat(result.get().getNombreCompleto()).isEqualTo("Test User");
    }
}
