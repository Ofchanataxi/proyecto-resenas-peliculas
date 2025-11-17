package edu.espe.proyectoresenasbackend.repository;

import edu.espe.proyectoresenasbackend.domain.Pelicula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests de PeliculaRepository")
class PeliculaRepositoryTest {

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Pelicula pelicula1;
    private Pelicula pelicula2;

    @BeforeEach
    void setUp() {
        peliculaRepository.deleteAll();

        pelicula1 = new Pelicula();
        pelicula1.setTitulo("Inception");
        pelicula1.setDirector("Christopher Nolan");
        pelicula1.setGenero("Ciencia Ficción");
        pelicula1.setDuracionMinutos(148);
        pelicula1.setFechaEstreno(LocalDate.of(2010, 7, 16));

        pelicula2 = new Pelicula();
        pelicula2.setTitulo("The Matrix");
        pelicula2.setDirector("Wachowski Brothers");
        pelicula2.setGenero("Ciencia Ficción");
        pelicula2.setDuracionMinutos(136);
        pelicula2.setFechaEstreno(LocalDate.of(1999, 3, 31));
    }

    @Test
    @DisplayName("Debe guardar una película correctamente")
    void testSavePelicula() {
        Pelicula savedPelicula = peliculaRepository.save(pelicula1);

        assertThat(savedPelicula).isNotNull();
        assertThat(savedPelicula.getId()).isNotNull();
        assertThat(savedPelicula.getTitulo()).isEqualTo("Inception");
        assertThat(savedPelicula.getDirector()).isEqualTo("Christopher Nolan");
    }

    @Test
    @DisplayName("Debe encontrar una película por ID")
    void testFindById() {
        Pelicula savedPelicula = entityManager.persistAndFlush(pelicula1);

        Optional<Pelicula> foundPelicula = peliculaRepository.findById(savedPelicula.getId());

        assertThat(foundPelicula).isPresent();
        assertThat(foundPelicula.get().getTitulo()).isEqualTo("Inception");
    }

    @Test
    @DisplayName("Debe listar todas las películas")
    void testFindAll() {
        entityManager.persist(pelicula1);
        entityManager.persist(pelicula2);
        entityManager.flush();

        List<Pelicula> peliculas = peliculaRepository.findAll();

        assertThat(peliculas).hasSize(2);
        assertThat(peliculas).extracting(Pelicula::getTitulo)
                .containsExactlyInAnyOrder("Inception", "The Matrix");
    }

    @Test
    @DisplayName("Debe actualizar una película existente")
    void testUpdatePelicula() {
        Pelicula savedPelicula = entityManager.persistAndFlush(pelicula1);

        savedPelicula.setTitulo("Inception: Director's Cut");
        savedPelicula.setDuracionMinutos(160);
        Pelicula updatedPelicula = peliculaRepository.save(savedPelicula);

        assertThat(updatedPelicula.getTitulo()).isEqualTo("Inception: Director's Cut");
        assertThat(updatedPelicula.getDuracionMinutos()).isEqualTo(160);
    }

    @Test
    @DisplayName("Debe eliminar una película por ID")
    void testDeleteById() {
        Pelicula savedPelicula = entityManager.persistAndFlush(pelicula1);
        Long peliculaId = savedPelicula.getId();

        peliculaRepository.deleteById(peliculaId);

        Optional<Pelicula> deletedPelicula = peliculaRepository.findById(peliculaId);
        assertThat(deletedPelicula).isEmpty();
    }

    @Test
    @DisplayName("Debe validar campos de fecha correctamente")
    void testDateFields() {
        Pelicula savedPelicula = peliculaRepository.save(pelicula1);

        assertThat(savedPelicula.getFechaEstreno()).isEqualTo(LocalDate.of(2010, 7, 16));
    }
}