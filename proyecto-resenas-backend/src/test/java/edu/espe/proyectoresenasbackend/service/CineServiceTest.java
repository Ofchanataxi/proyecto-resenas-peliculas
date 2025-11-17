package edu.espe.proyectoresenasbackend.service;

import edu.espe.proyectoresenasbackend.domain.Cine;
import edu.espe.proyectoresenasbackend.dto.CineRequest;
import edu.espe.proyectoresenasbackend.dto.CineResponse;
import edu.espe.proyectoresenasbackend.repository.CineRepository;
import edu.espe.proyectoresenasbackend.service.impl.CineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de CineService")
class CineServiceTest {

    @Mock
    private CineRepository cineRepository;

    @InjectMocks
    private CineServiceImpl cineService;

    private CineRequest cineRequest;
    private Cine cine;
    private Cine cine2;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        cineRequest = new CineRequest();
        cineRequest.setNombre("Cinemark");
        cineRequest.setDireccion("Av. Amazonas y Naciones Unidas");
        cineRequest.setCiudad("Quito");

        cine = new Cine();
        cine.setId(1L);
        cine.setNombre("Cinemark");
        cine.setDireccion("Av. Amazonas y Naciones Unidas");
        cine.setCiudad("Quito");

        cine2 = new Cine();
        cine2.setId(2L);
        cine2.setNombre("Supercines");
        cine2.setDireccion("CC El Recreo");
        cine2.setCiudad("Quito");
    }

    @Test
    @DisplayName("Debe crear un cine exitosamente")
    void testCreate() {
        // Simular comportamiento típico de JPA: asignar ID al objeto recibido
        when(cineRepository.save(any(Cine.class))).thenAnswer(invocation -> {
            Cine c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        // When
        CineResponse response = cineService.create(cineRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Cinemark");
        assertThat(response.getDireccion()).isEqualTo("Av. Amazonas y Naciones Unidas");
        assertThat(response.getCiudad()).isEqualTo("Quito");

        verify(cineRepository, times(1)).save(any(Cine.class));
    }

    @Test
    @DisplayName("Debe obtener un cine por ID exitosamente")
    void testGet() {
        // Given
        when(cineRepository.findById(1L)).thenReturn(Optional.of(cine));

        // When
        CineResponse response = cineService.get(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Cinemark");

        verify(cineRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el cine no existe")
    void testGetNotFound() {
        // Given
        when(cineRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cineService.get(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cine no encontrado");

        verify(cineRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe listar todos los cines")
    void testList() {
        // Given
        List<Cine> cines = Arrays.asList(cine, cine2);
        when(cineRepository.findAll()).thenReturn(cines);

        // When
        List<CineResponse> responses = cineService.list();

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getNombre()).isEqualTo("Cinemark");
        assertThat(responses.get(1).getNombre()).isEqualTo("Supercines");

        verify(cineRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay cines")
    void testListEmpty() {
        // Given
        when(cineRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<CineResponse> responses = cineService.list();

        // Then
        assertThat(responses).isEmpty();

        verify(cineRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe actualizar un cine exitosamente")
    void testUpdate() {
        // Given
        CineRequest updateRequest = new CineRequest();
        updateRequest.setNombre("Cinemark Premium");
        updateRequest.setDireccion("Nueva Dirección");
        updateRequest.setCiudad("Guayaquil");

        Cine updatedCine = new Cine();
        updatedCine.setId(1L);
        updatedCine.setNombre("Cinemark Premium");
        updatedCine.setDireccion("Nueva Dirección");
        updatedCine.setCiudad("Guayaquil");

        when(cineRepository.findById(1L)).thenReturn(Optional.of(cine));
        when(cineRepository.save(any(Cine.class))).thenReturn(updatedCine);

        // When
        CineResponse response = cineService.update(1L, updateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo("Cinemark Premium");
        assertThat(response.getDireccion()).isEqualTo("Nueva Dirección");
        assertThat(response.getCiudad()).isEqualTo("Guayaquil");

        verify(cineRepository, times(1)).findById(1L);
        verify(cineRepository, times(1)).save(any(Cine.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar un cine inexistente")
    void testUpdateNotFound() {
        // Given
        when(cineRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cineService.update(999L, cineRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cine no encontrado");

        verify(cineRepository, times(1)).findById(999L);
        verify(cineRepository, never()).save(any(Cine.class));
    }

    @Test
    @DisplayName("Debe eliminar un cine exitosamente")
    void testDelete() {
        // Given
        doNothing().when(cineRepository).deleteById(1L);

        // When
        cineService.delete(1L);

        // Then
        verify(cineRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe manejar la eliminación de un cine inexistente")
    void testDeleteNotFound() {
        // Given
        doThrow(new RuntimeException("Cine no encontrado")).when(cineRepository).deleteById(999L);

        // When & Then
        assertThatThrownBy(() -> cineService.delete(999L))
                .isInstanceOf(RuntimeException.class);

        verify(cineRepository, times(1)).deleteById(999L);
    }

    @Test
    @DisplayName("Debe mapear correctamente de Request a Entity")
    void testMappingRequestToEntity() {
        // Given
        when(cineRepository.save(any(Cine.class))).thenAnswer(invocation -> {
            Cine savedCine = invocation.getArgument(0);
            savedCine.setId(1L);
            return savedCine;
        });

        // When
        CineResponse response = cineService.create(cineRequest);

        // Then
        verify(cineRepository).save(argThat(c ->
                c.getNombre().equals("Cinemark") &&
                        c.getDireccion().equals("Av. Amazonas y Naciones Unidas") &&
                        c.getCiudad().equals("Quito")
        ));
    }

    @Test
    @DisplayName("Debe mapear correctamente de Entity a Response")
    void testMappingEntityToResponse() {
        // Given
        when(cineRepository.findById(1L)).thenReturn(Optional.of(cine));

        // When
        CineResponse response = cineService.get(1L);

        // Then
        assertThat(response.getId()).isEqualTo(cine.getId());
        assertThat(response.getNombre()).isEqualTo(cine.getNombre());
        assertThat(response.getDireccion()).isEqualTo(cine.getDireccion());
        assertThat(response.getCiudad()).isEqualTo(cine.getCiudad());
    }
}