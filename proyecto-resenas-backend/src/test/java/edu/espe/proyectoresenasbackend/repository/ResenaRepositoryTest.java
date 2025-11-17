package edu.espe.proyectoresenasbackend.repository;

import edu.espe.proyectoresenasbackend.domain.Pelicula;
import edu.espe.proyectoresenasbackend.domain.Resena;
import edu.espe.proyectoresenasbackend.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests de ResenaRepository")
class ResenaRepositoryTest {

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Usuario usuario;
    private Pelicula pelicula;
    private Resena resena1;
    private Resena resena2;

    @BeforeEach
    void setUp() {
        resenaRepository.deleteAll();

        // Crear usuario de prueba
        usuario = new Usuario();
        usuario.setNombreCompleto("Juan Pérez");
        usuario.setEmail("juan@test.com");
        usuario.setContrasena("password123");
        usuario.setActivo(true);
        usuario = entityManager.persist(usuario);

        // Crear película de prueba
        pelicula = new Pelicula();
        pelicula.setTitulo("Inception");
        pelicula.setDirector("Christopher Nolan");
        pelicula.setGenero("Ciencia Ficción");
        pelicula.setDuracionMinutos(148);
        pelicula.setFechaEstreno(LocalDate.of(2010, 7, 16));
        pelicula = entityManager.persist(pelicula);

        // Crear reseñas de prueba
        resena1 = new Resena();
        resena1.setComentario("Excelente película, muy bien dirigida");
        resena1.setCalificacion(5);
        resena1.setUsuario(usuario);
        resena1.setPelicula(pelicula);
        resena1.setFechaCreacion(LocalDateTime.now());

        resena2 = new Resena();
        resena2.setComentario("Buena pero confusa en algunas partes");
        resena2.setCalificacion(4);
        resena2.setUsuario(usuario);
        resena2.setPelicula(pelicula);
        resena2.setFechaCreacion(LocalDateTime.now());

        entityManager.flush();
    }

    @Test
    @DisplayName("Debe guardar una reseña correctamente")
    void testSaveResena() {
        Resena savedResena = resenaRepository.save(resena1);

        assertThat(savedResena).isNotNull();
        assertThat(savedResena.getId()).isNotNull();
        assertThat(savedResena.getComentario()).isEqualTo("Excelente película, muy bien dirigida");
        assertThat(savedResena.getCalificacion()).isEqualTo(5);
        assertThat(savedResena.getUsuario()).isNotNull();
        assertThat(savedResena.getPelicula()).isNotNull();
    }

    @Test
    @DisplayName("Debe encontrar una reseña por ID")
    void testFindById() {
        Resena savedResena = entityManager.persistAndFlush(resena1);

        Optional<Resena> foundResena = resenaRepository.findById(savedResena.getId());

        assertThat(foundResena).isPresent();
        assertThat(foundResena.get().getComentario()).contains("Excelente");
    }

    @Test
    @DisplayName("Debe listar todas las reseñas")
    void testFindAll() {
        entityManager.persist(resena1);
        entityManager.persist(resena2);
        entityManager.flush();

        List<Resena> resenas = resenaRepository.findAll();

        assertThat(resenas).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Debe mantener las relaciones con Usuario y Película")
    void testRelationships() {
        Resena savedResena = resenaRepository.save(resena1);
        entityManager.flush();
        entityManager.clear();

        Resena foundResena = resenaRepository.findById(savedResena.getId()).orElseThrow();

        assertThat(foundResena.getUsuario()).isNotNull();
        assertThat(foundResena.getUsuario().getEmail()).isEqualTo("juan@test.com");
        assertThat(foundResena.getPelicula()).isNotNull();
        assertThat(foundResena.getPelicula().getTitulo()).isEqualTo("Inception");
    }

    @Test
    @DisplayName("Debe actualizar una reseña existente")
    void testUpdateResena() {
        Resena savedResena = entityManager.persistAndFlush(resena1);

        savedResena.setComentario("Actualizado: Una obra maestra");
        savedResena.setCalificacion(5);
        Resena updatedResena = resenaRepository.save(savedResena);

        assertThat(updatedResena.getComentario()).contains("Actualizado");
        assertThat(updatedResena.getCalificacion()).isEqualTo(5);
    }

    @Test
    @DisplayName("Debe eliminar una reseña por ID")
    void testDeleteById() {
        Resena savedResena = entityManager.persistAndFlush(resena1);
        Long resenaId = savedResena.getId();

        resenaRepository.deleteById(resenaId);

        Optional<Resena> deletedResena = resenaRepository.findById(resenaId);
        assertThat(deletedResena).isEmpty();
    }

    @Test
    @DisplayName("Debe guardar la fecha de creación automáticamente")
    void testFechaCreacion() {
        Resena savedResena = resenaRepository.save(resena1);

        assertThat(savedResena.getFechaCreacion()).isNotNull();
        assertThat(savedResena.getFechaCreacion()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}