package edu.espe.proyectoresenasbackend.service;

import edu.espe.proyectoresenasbackend.domain.Pelicula;
import edu.espe.proyectoresenasbackend.dto.PeliculaRequest;
import edu.espe.proyectoresenasbackend.dto.PeliculaResponse;
import edu.espe.proyectoresenasbackend.repository.PeliculaRepository;
import edu.espe.proyectoresenasbackend.service.impl.PeliculaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de PeliculaService")
class PeliculaServiceTest {

    @Mock
    private PeliculaRepository peliculaRepository;

    @InjectMocks
    private PeliculaServiceImpl peliculaService;

    private PeliculaRequest peliculaRequest;
    private Pelicula pelicula;
    private Pelicula pelicula2;

    @BeforeEach
    void setUp() {
        peliculaRequest = new PeliculaRequest();
        peliculaRequest.setTitulo("Inception");
        peliculaRequest.setDirector("Christopher Nolan");
        peliculaRequest.setGenero("Ciencia Ficción");
        peliculaRequest.setDuracionMinutos(148);
        peliculaRequest.setFechaEstreno(LocalDate.of(2010, 7, 16));

        pelicula = new Pelicula();
        pelicula.setId(1L);
        pelicula.setTitulo("Inception");
        pelicula.setDirector("Christopher Nolan");
        pelicula.setGenero("Ciencia Ficción");
        pelicula.setDuracionMinutos(148);
        pelicula.setFechaEstreno(LocalDate.of(2010, 7, 16));

        pelicula2 = new Pelicula();
        pelicula2.setId(2L);
        pelicula2.setTitulo("The Matrix");
        pelicula2.setDirector("Wachowski Brothers");
        pelicula2.setGenero("Ciencia Ficción");
        pelicula2.setDuracionMinutos(136);
        pelicula2.setFechaEstreno(LocalDate.of(1999, 3, 31));
    }

    @Test
    @DisplayName("Debe crear una película exitosamente")
    void testCreate() {
        when(peliculaRepository.save(any(Pelicula.class))).thenAnswer(invocation -> {
            Pelicula p = invocation.getArgument(0);
            p.setId(1L); // Simula comportamiento real de JPA
            return p;
        });

        PeliculaResponse response = peliculaService.create(peliculaRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitulo()).isEqualTo("Inception");
        assertThat(response.getDirector()).isEqualTo("Christopher Nolan");
        assertThat(response.getGenero()).isEqualTo("Ciencia Ficción");
        assertThat(response.getDuracionMinutos()).isEqualTo(148);
        assertThat(response.getFechaEstreno()).isEqualTo(LocalDate.of(2010, 7, 16));

        verify(peliculaRepository, times(1)).save(any(Pelicula.class));
    }


    @Test
    @DisplayName("Debe obtener una película por ID exitosamente")
    void testGet() {
        when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));

        PeliculaResponse response = peliculaService.get(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitulo()).isEqualTo("Inception");

        verify(peliculaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la película no existe")
    void testGetNotFound() {
        when(peliculaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> peliculaService.get(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Película no encontrada");

        verify(peliculaRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe listar todas las películas")
    void testList() {
        List<Pelicula> peliculas = Arrays.asList(pelicula, pelicula2);
        when(peliculaRepository.findAll()).thenReturn(peliculas);

        List<PeliculaResponse> responses = peliculaService.list();

        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitulo()).isEqualTo("Inception");
        assertThat(responses.get(1).getTitulo()).isEqualTo("The Matrix");

        verify(peliculaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe actualizar una película exitosamente")
    void testUpdate() {
        PeliculaRequest updateRequest = new PeliculaRequest();
        updateRequest.setTitulo("Inception: Director's Cut");
        updateRequest.setDirector("Christopher Nolan");
        updateRequest.setGenero("Ciencia Ficción");
        updateRequest.setDuracionMinutos(160);
        updateRequest.setFechaEstreno(LocalDate.of(2010, 7, 16));

        Pelicula updatedPelicula = new Pelicula();
        updatedPelicula.setId(1L);
        updatedPelicula.setTitulo("Inception: Director's Cut");
        updatedPelicula.setDirector("Christopher Nolan");
        updatedPelicula.setGenero("Ciencia Ficción");
        updatedPelicula.setDuracionMinutos(160);
        updatedPelicula.setFechaEstreno(LocalDate.of(2010, 7, 16));

        when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));
        when(peliculaRepository.save(any(Pelicula.class))).thenReturn(updatedPelicula);

        PeliculaResponse response = peliculaService.update(1L, updateRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTitulo()).isEqualTo("Inception: Director's Cut");
        assertThat(response.getDuracionMinutos()).isEqualTo(160);

        verify(peliculaRepository, times(1)).findById(1L);
        verify(peliculaRepository, times(1)).save(any(Pelicula.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar una película inexistente")
    void testUpdateNotFound() {
        when(peliculaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> peliculaService.update(999L, peliculaRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Película no encontrada");

        verify(peliculaRepository, times(1)).findById(999L);
        verify(peliculaRepository, never()).save(any(Pelicula.class));
    }

    @Test
    @DisplayName("Debe eliminar una película exitosamente")
    void testDelete() {
        doNothing().when(peliculaRepository).deleteById(1L);

        peliculaService.delete(1L);

        verify(peliculaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe validar correctamente campos de fecha")
    void testDateValidation() {
        when(peliculaRepository.save(any(Pelicula.class))).thenReturn(pelicula);

        PeliculaResponse response = peliculaService.create(peliculaRequest);

        assertThat(response.getFechaEstreno()).isEqualTo(LocalDate.of(2010, 7, 16));
        assertThat(response.getFechaEstreno()).isBefore(LocalDate.now());
    }

    @Test
    @DisplayName("Debe mapear correctamente todos los campos")
    void testCompleteMapping() {
        when(peliculaRepository.save(any(Pelicula.class))).thenReturn(pelicula);

        PeliculaResponse response = peliculaService.create(peliculaRequest);

        assertThat(response.getTitulo()).isEqualTo(peliculaRequest.getTitulo());
        assertThat(response.getDirector()).isEqualTo(peliculaRequest.getDirector());
        assertThat(response.getGenero()).isEqualTo(peliculaRequest.getGenero());
        assertThat(response.getDuracionMinutos()).isEqualTo(peliculaRequest.getDuracionMinutos());
        assertThat(response.getFechaEstreno()).isEqualTo(peliculaRequest.getFechaEstreno());
    }
}