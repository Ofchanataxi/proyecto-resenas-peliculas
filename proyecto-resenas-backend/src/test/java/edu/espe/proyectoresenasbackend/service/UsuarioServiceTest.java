package edu.espe.proyectoresenasbackend.service;

import edu.espe.proyectoresenasbackend.domain.Usuario;
import edu.espe.proyectoresenasbackend.dto.UsuarioRequestData;
import edu.espe.proyectoresenasbackend.repository.UsuarioRepository;
import edu.espe.proyectoresenasbackend.service.impl.UsuarioServiceImpl;
import edu.espe.proyectoresenasbackend.web.advice.ConflictException;
import edu.espe.proyectoresenasbackend.web.advice.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@Import(UsuarioServiceImpl.class)
public class UsuarioServiceTest {
    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioRepository repository;

    @Test
    void shouldNotAllowDuplicateEmail() {
        // 1. Creamos un usuario existente
        Usuario existing = new Usuario();
        existing.setNombreCompleto("Usuario Existente");
        existing.setEmail("test@example.com");
        existing.setContrasena("pass1");
        existing.setActivo(true);
        repository.save(existing);

        // 2. Creamos un request con el MISMO email
        UsuarioRequestData req = new UsuarioRequestData();
        req.setNombreCompleto("Nuevo Usuario Duplicado");
        req.setEmail("test@example.com"); // Email duplicado
        req.setContrasena("pass2");
        req.setActivo(true);

        // 3. Verificamos que lance la excepción de conflicto
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(ConflictException.class);

        // 4. Verificamos que la base de datos no haya cambiado
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void inexistentIdShouldThrowNotFound() {
        Long nonexistentId = 999L;

        // Verificamos que buscar por un ID que no existe lance NotFoundException
        assertThatThrownBy(() -> service.getById(nonexistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    void deactivateUsuarioShouldSetActivoFalse() {
        // 1. Creamos un usuario activo
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto("Usuario Activo");
        usuario.setEmail("activo@espe.edu.ec");
        usuario.setContrasena("pass123");
        usuario.setActivo(true);
        repository.save(usuario);

        // 2. Llamamos al servicio para desactivar
        service.deactivate(usuario.getId());

        // 3. Verificamos en la BD que el estado sea 'false'
        Usuario updated = repository.findById(usuario.getId()).orElseThrow();
        assertThat(updated.getActivo()).isFalse();
    }

    @Test
    void getStatsShouldReturnCorrectCounts() {
        // 1. Creamos 2 usuarios activos
        Usuario active1 = new Usuario();
        active1.setNombreCompleto("Activo Uno");
        active1.setEmail("active1@example.com");
        active1.setContrasena("p");
        active1.setActivo(true);
        repository.save(active1);

        Usuario active2 = new Usuario();
        active2.setNombreCompleto("Activo Dos");
        active2.setEmail("active2@example.com");
        active2.setContrasena("p");
        active2.setActivo(true);
        repository.save(active2);

        // 2. Creamos 1 usuario inactivo
        Usuario inactive1 = new Usuario();
        inactive1.setNombreCompleto("Inactivo Uno");
        inactive1.setEmail("inactive1@example.com");
        inactive1.setContrasena("p");
        inactive1.setActivo(false);
        repository.save(inactive1);

        // 3. Pedimos las estadísticas
        Object stats = service.getStats();

        // 4. Verificamos los conteos
        assertThat(stats).isInstanceOf(java.util.Map.class);
        java.util.Map<?, ?> statsMap = (java.util.Map<?, ?>) stats;
        assertThat(statsMap.get("total")).isEqualTo(3L);
        assertThat(statsMap.get("active")).isEqualTo(2L);
        assertThat(statsMap.get("inactive")).isEqualTo(1L);
    }
}
