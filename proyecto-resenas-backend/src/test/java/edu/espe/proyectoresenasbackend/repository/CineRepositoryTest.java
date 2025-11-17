package edu.espe.proyectoresenasbackend.repository;

import edu.espe.proyectoresenasbackend.domain.Cine;
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
@DisplayName("Tests de CineRepository")
class CineRepositoryTest {

    @Autowired
    private CineRepository cineRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Cine cine1;
    private Cine cine2;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada test
        cineRepository.deleteAll();

        // Crear datos de prueba
        cine1 = new Cine();
        cine1.setNombre("Cinemark");
        cine1.setDireccion("Av. Amazonas y Naciones Unidas");
        cine1.setCiudad("Quito");

        cine2 = new Cine();
        cine2.setNombre("Supercines");
        cine2.setDireccion("CC El Recreo");
        cine2.setCiudad("Quito");
    }

    @Test
    @DisplayName("Debe guardar un cine correctamente")
    void testSaveCine() {
        // When
        Cine savedCine = cineRepository.save(cine1);

        // Then
        assertThat(savedCine).isNotNull();
        assertThat(savedCine.getId()).isNotNull();
        assertThat(savedCine.getNombre()).isEqualTo("Cinemark");
        assertThat(savedCine.getDireccion()).isEqualTo("Av. Amazonas y Naciones Unidas");
        assertThat(savedCine.getCiudad()).isEqualTo("Quito");
    }

    @Test
    @DisplayName("Debe encontrar un cine por ID")
    void testFindById() {
        // Given
        Cine savedCine = entityManager.persistAndFlush(cine1);

        // When
        Optional<Cine> foundCine = cineRepository.findById(savedCine.getId());

        // Then
        assertThat(foundCine).isPresent();
        assertThat(foundCine.get().getNombre()).isEqualTo("Cinemark");
    }

    @Test
    @DisplayName("Debe retornar empty cuando el cine no existe")
    void testFindByIdNotFound() {
        // When
        Optional<Cine> foundCine = cineRepository.findById(999L);

        // Then
        assertThat(foundCine).isEmpty();
    }

    @Test
    @DisplayName("Debe listar todos los cines")
    void testFindAll() {
        // Given
        entityManager.persist(cine1);
        entityManager.persist(cine2);
        entityManager.flush();

        // When
        List<Cine> cines = cineRepository.findAll();

        // Then
        assertThat(cines).hasSize(2);
        assertThat(cines).extracting(Cine::getNombre)
                .containsExactlyInAnyOrder("Cinemark", "Supercines");
    }

    @Test
    @DisplayName("Debe actualizar un cine existente")
    void testUpdateCine() {
        // Given
        Cine savedCine = entityManager.persistAndFlush(cine1);

        // When
        savedCine.setNombre("Cinemark Premium");
        savedCine.setCiudad("Guayaquil");
        Cine updatedCine = cineRepository.save(savedCine);

        // Then
        assertThat(updatedCine.getNombre()).isEqualTo("Cinemark Premium");
        assertThat(updatedCine.getCiudad()).isEqualTo("Guayaquil");
    }

    @Test
    @DisplayName("Debe eliminar un cine por ID")
    void testDeleteById() {
        // Given
        Cine savedCine = entityManager.persistAndFlush(cine1);
        Long cineId = savedCine.getId();

        // When
        cineRepository.deleteById(cineId);

        // Then
        Optional<Cine> deletedCine = cineRepository.findById(cineId);
        assertThat(deletedCine).isEmpty();
    }

    @Test
    @DisplayName("Debe contar correctamente el número de cines")
    void testCount() {
        // Given
        entityManager.persist(cine1);
        entityManager.persist(cine2);
        entityManager.flush();

        // When
        long count = cineRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe verificar si existe un cine por ID")
    void testExistsById() {
        // Given
        Cine savedCine = entityManager.persistAndFlush(cine1);

        // When
        boolean exists = cineRepository.existsById(savedCine.getId());
        boolean notExists = cineRepository.existsById(999L);

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}