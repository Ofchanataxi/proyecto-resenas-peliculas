package edu.espe.proyectoresenasbackend.service;

import edu.espe.proyectoresenasbackend.domain.Pelicula;
import edu.espe.proyectoresenasbackend.domain.Resena;
import edu.espe.proyectoresenasbackend.domain.Usuario;
import edu.espe.proyectoresenasbackend.dto.ResenaRequest;
import edu.espe.proyectoresenasbackend.dto.ResenaResponse;
import edu.espe.proyectoresenasbackend.repository.PeliculaRepository;
import edu.espe.proyectoresenasbackend.repository.ResenaRepository;
import edu.espe.proyectoresenasbackend.repository.UsuarioRepository;
import edu.espe.proyectoresenasbackend.service.impl.ResenaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de ResenaService")
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PeliculaRepository peliculaRepository;

    @InjectMocks
    private ResenaServiceImpl resenaService;

    private ResenaRequest resenaRequest;
    private Resena resena;
    private Usuario usuario;
    private Pelicula pelicula;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreCompleto("Juan Pérez");
        usuario.setEmail("juan@test.com");
        usuario.setContrasena("password123");
        usuario.setActivo(true);

        pelicula = new Pelicula();
        pelicula.setId(1L);
        pelicula.setTitulo("Inception");
        pelicula.setDirector("Christopher Nolan");
        pelicula.setGenero("Ciencia Ficción");
        pelicula.setDuracionMinutos(148);
        pelicula.setFechaEstreno(LocalDate.of(2010, 7, 16));

        resenaRequest = new ResenaRequest();
        resenaRequest.setComentario("Excelente película");
        resenaRequest.setCalificacion(5);
        resenaRequest.setUsuarioId(1L);
        resenaRequest.setPeliculaId(1L);

        resena = new Resena();
        resena.setId(1L);
        resena.setComentario("Excelente película");
        resena.setCalificacion(5);
        resena.setUsuario(usuario);
        resena.setPelicula(pelicula);
        resena.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    @DisplayName("Debe crear una reseña exitosamente")
    void testCreate() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));

        // Simular JPA: asignar ID al mismo objeto que se guardó
        when(resenaRepository.save(any(Resena.class))).thenAnswer(invocation -> {
            Resena r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        ResenaResponse response = resenaService.create(resenaRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getComentario()).isEqualTo("Excelente película");
        assertThat(response.getCalificacion()).isEqualTo(5);
        assertThat(response.getUsuarioId()).isEqualTo(1L);
        assertThat(response.getPeliculaId()).isEqualTo(1L);

        verify(usuarioRepository, times(1)).findById(1L);
        verify(peliculaRepository, times(1)).findById(1L);
        verify(resenaRepository, times(1)).save(any(Resena.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void testCreateUsuarioNotFound() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resenaService.create(resenaRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(usuarioRepository, times(1)).findById(1L);
        verify(peliculaRepository, never()).findById(anyLong());
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la película no existe")
    void testCreatePeliculaNotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(peliculaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resenaService.create(resenaRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Película no encontrada");

        verify(usuarioRepository, times(1)).findById(1L);
        verify(peliculaRepository, times(1)).findById(1L);
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    @DisplayName("Debe obtener una reseña por ID exitosamente")
    void testGet() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        ResenaResponse response = resenaService.get(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getComentario()).isEqualTo("Excelente película");

        verify(resenaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la reseña no existe")
    void testGetNotFound() {
        when(resenaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resenaService.get(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Reseña no encontrada");

        verify(resenaRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe listar todas las reseñas")
    void testList() {
        Resena resena2 = new Resena();
        resena2.setId(2L);
        resena2.setComentario("Buena película");
        resena2.setCalificacion(4);
        resena2.setUsuario(usuario);
        resena2.setPelicula(pelicula);
        resena2.setFechaCreacion(LocalDateTime.now());

        List<Resena> resenas = Arrays.asList(resena, resena2);
        when(resenaRepository.findAll()).thenReturn(resenas);

        List<ResenaResponse> responses = resenaService.list();

        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getComentario()).isEqualTo("Excelente película");
        assertThat(responses.get(1).getComentario()).isEqualTo("Buena película");

        verify(resenaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe actualizar una reseña exitosamente")
    void testUpdate() {
        ResenaRequest updateRequest = new ResenaRequest();
        updateRequest.setComentario("Obra maestra del cine");
        updateRequest.setCalificacion(5);
        updateRequest.setUsuarioId(1L);
        updateRequest.setPeliculaId(1L);

        Resena updatedResena = new Resena();
        updatedResena.setId(1L);
        updatedResena.setComentario("Obra maestra del cine");
        updatedResena.setCalificacion(5);
        updatedResena.setUsuario(usuario);
        updatedResena.setPelicula(pelicula);
        updatedResena.setFechaCreacion(LocalDateTime.now());

        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));
        when(resenaRepository.save(any(Resena.class))).thenReturn(updatedResena);

        ResenaResponse response = resenaService.update(1L, updateRequest);

        assertThat(response).isNotNull();
        assertThat(response.getComentario()).isEqualTo("Obra maestra del cine");

        verify(resenaRepository, times(1)).findById(1L);
        verify(resenaRepository, times(1)).save(any(Resena.class));
    }

    @Test
    @DisplayName("Debe eliminar una reseña exitosamente")
    void testDelete() {
        doNothing().when(resenaRepository).deleteById(1L);

        resenaService.delete(1L);

        verify(resenaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe validar que la fecha de creación esté presente")
    void testFechaCreacion() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        ResenaResponse response = resenaService.get(1L);

        assertThat(response.getFechaCreacion()).isNotNull();
        assertThat(response.getFechaCreacion()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Debe mapear correctamente las relaciones")
    void testRelationshipsMapping() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);

        ResenaResponse response = resenaService.create(resenaRequest);

        assertThat(response.getUsuarioId()).isEqualTo(usuario.getId());
        assertThat(response.getPeliculaId()).isEqualTo(pelicula.getId());
    }
}