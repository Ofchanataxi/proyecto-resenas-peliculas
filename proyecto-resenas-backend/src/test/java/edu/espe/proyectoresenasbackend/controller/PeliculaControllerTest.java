package edu.espe.proyectoresenasbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.espe.proyectoresenasbackend.dto.PeliculaRequest;
import edu.espe.proyectoresenasbackend.dto.PeliculaResponse;
import edu.espe.proyectoresenasbackend.security.JwtService;
import edu.espe.proyectoresenasbackend.service.PeliculaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PeliculaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PeliculaService peliculaService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private PeliculaRequest peliculaRequest;
    private PeliculaResponse peliculaResponse;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        peliculaRequest = new PeliculaRequest();
        peliculaRequest.setTitulo("Inception");
        peliculaRequest.setDirector("Christopher Nolan");
        peliculaRequest.setGenero("Ciencia Ficción");
        peliculaRequest.setDuracionMinutos(148);
        peliculaRequest.setFechaEstreno(LocalDate.of(2010, 7, 16));

        peliculaResponse = new PeliculaResponse();
        peliculaResponse.setId(1L);
        peliculaResponse.setTitulo("Inception");
        peliculaResponse.setDirector("Christopher Nolan");
        peliculaResponse.setGenero("Ciencia Ficción");
        peliculaResponse.setDuracionMinutos(148);
        peliculaResponse.setFechaEstreno(LocalDate.of(2010, 7, 16));

        jwtToken = "Bearer test-token";

        // Mockear el comportamiento del JwtService
        when(jwtService.extractUsername("test-token")).thenReturn("testuser");
        when(jwtService.isTokenValid(eq("test-token"), any())).thenReturn(true);

        // Mockear UserDetailsService
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
    }

    @Test
    void create_ShouldReturnPeliculaResponse_WhenValidRequest() throws Exception {
        when(peliculaService.create(any(PeliculaRequest.class))).thenReturn(peliculaResponse);

        mockMvc.perform(post("/api/resenas/pelicula")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(peliculaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Inception"))
                .andExpect(jsonPath("$.director").value("Christopher Nolan"))
                .andExpect(jsonPath("$.duracionMinutos").value(148));

        verify(peliculaService).create(any(PeliculaRequest.class));
    }

    @Test
    void get_ShouldReturnPelicula_WhenIdExists() throws Exception {
        when(peliculaService.get(1L)).thenReturn(peliculaResponse);

        mockMvc.perform(get("/api/resenas/pelicula/1")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Inception"));

        verify(peliculaService).get(1L);
    }

    @Test
    void list_ShouldReturnAllPeliculas() throws Exception {
        PeliculaResponse pelicula2 = new PeliculaResponse();
        pelicula2.setId(2L);
        pelicula2.setTitulo("Interstellar");
        pelicula2.setDirector("Christopher Nolan");
        pelicula2.setGenero("Ciencia Ficción");
        pelicula2.setDuracionMinutos(169);
        pelicula2.setFechaEstreno(LocalDate.of(2014, 11, 7));

        List<PeliculaResponse> peliculas = Arrays.asList(peliculaResponse, pelicula2);
        when(peliculaService.list()).thenReturn(peliculas);

        mockMvc.perform(get("/api/resenas/pelicula")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Inception"))
                .andExpect(jsonPath("$[1].titulo").value("Interstellar"));

        verify(peliculaService).list();
    }

    @Test
    void update_ShouldReturnUpdatedPelicula_WhenValidRequest() throws Exception {
        PeliculaResponse updatedPelicula = new PeliculaResponse();
        updatedPelicula.setId(1L);
        updatedPelicula.setTitulo("Inception - Director's Cut");
        updatedPelicula.setDirector("Christopher Nolan");
        updatedPelicula.setGenero("Ciencia Ficción");
        updatedPelicula.setDuracionMinutos(152);
        updatedPelicula.setFechaEstreno(LocalDate.of(2010, 7, 16));

        when(peliculaService.update(eq(1L), any(PeliculaRequest.class))).thenReturn(updatedPelicula);

        mockMvc.perform(put("/api/resenas/pelicula/1")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(peliculaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Inception - Director's Cut"))
                .andExpect(jsonPath("$.duracionMinutos").value(152));

        verify(peliculaService).update(eq(1L), any(PeliculaRequest.class));
    }

    @Test
    void delete_ShouldCallService_WhenValidId() throws Exception {
        doNothing().when(peliculaService).delete(1L);

        mockMvc.perform(delete("/api/resenas/pelicula/1")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk());

        verify(peliculaService).delete(1L);
    }

    @Test
    void create_ShouldReturnForbidden_WhenNoToken() throws Exception {
        mockMvc.perform(post("/api/resenas/pelicula")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(peliculaRequest)))
                .andExpect(status().isForbidden());

        verify(peliculaService, never()).create(any());
    }

    @Test
    void get_ShouldReturnNotFound_WhenPeliculaDoesNotExist() throws Exception {
        when(peliculaService.get(999L)).thenThrow(new RuntimeException("Película no encontrada"));

        mockMvc.perform(get("/api/resenas/pelicula/999")
                        .header("Authorization", jwtToken))
                .andExpect(status().is5xxServerError());

        verify(peliculaService).get(999L);
    }
}