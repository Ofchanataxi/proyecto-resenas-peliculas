package edu.espe.proyectoresenasbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.espe.proyectoresenasbackend.dto.ResenaRequest;
import edu.espe.proyectoresenasbackend.dto.ResenaResponse;
import edu.espe.proyectoresenasbackend.security.JwtService;
import edu.espe.proyectoresenasbackend.service.ResenaService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ResenaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ResenaService resenaService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private ResenaRequest resenaRequest;
    private ResenaResponse resenaResponse;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        resenaRequest = new ResenaRequest();
        resenaRequest.setComentario("Excelente película, muy entretenida");
        resenaRequest.setCalificacion(5);
        resenaRequest.setUsuarioId(1L);
        resenaRequest.setPeliculaId(1L);

        resenaResponse = new ResenaResponse();
        resenaResponse.setId(1L);
        resenaResponse.setComentario("Excelente película, muy entretenida");
        resenaResponse.setCalificacion(5);
        resenaResponse.setFechaCreacion(LocalDateTime.now());
        resenaResponse.setUsuarioId(1L);
        resenaResponse.setPeliculaId(1L);

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
    void create_ShouldReturnResenaResponse_WhenValidRequest() throws Exception {
        when(resenaService.create(any(ResenaRequest.class))).thenReturn(resenaResponse);

        mockMvc.perform(post("/api/resenas/resena")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resenaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comentario").value("Excelente película, muy entretenida"))
                .andExpect(jsonPath("$.calificacion").value(5))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.peliculaId").value(1));

        verify(resenaService).create(any(ResenaRequest.class));
    }

    @Test
    void get_ShouldReturnResena_WhenIdExists() throws Exception {
        when(resenaService.get(1L)).thenReturn(resenaResponse);

        mockMvc.perform(get("/api/resenas/resena/1")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comentario").value("Excelente película, muy entretenida"));

        verify(resenaService).get(1L);
    }

    @Test
    void list_ShouldReturnAllResenas() throws Exception {
        ResenaResponse resena2 = new ResenaResponse();
        resena2.setId(2L);
        resena2.setComentario("Buena película");
        resena2.setCalificacion(4);
        resena2.setFechaCreacion(LocalDateTime.now());
        resena2.setUsuarioId(2L);
        resena2.setPeliculaId(1L);

        List<ResenaResponse> resenas = Arrays.asList(resenaResponse, resena2);
        when(resenaService.list()).thenReturn(resenas);

        mockMvc.perform(get("/api/resenas/resena")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].calificacion").value(5))
                .andExpect(jsonPath("$[1].calificacion").value(4));

        verify(resenaService).list();
    }

    @Test
    void update_ShouldReturnUpdatedResena_WhenValidRequest() throws Exception {
        ResenaResponse updatedResena = new ResenaResponse();
        updatedResena.setId(1L);
        updatedResena.setComentario("Película increíble, la mejor del año");
        updatedResena.setCalificacion(5);
        updatedResena.setFechaCreacion(LocalDateTime.now());
        updatedResena.setUsuarioId(1L);
        updatedResena.setPeliculaId(1L);

        when(resenaService.update(eq(1L), any(ResenaRequest.class))).thenReturn(updatedResena);

        mockMvc.perform(put("/api/resenas/resena/1")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resenaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comentario").value("Película increíble, la mejor del año"));

        verify(resenaService).update(eq(1L), any(ResenaRequest.class));
    }

    @Test
    void delete_ShouldCallService_WhenValidId() throws Exception {
        doNothing().when(resenaService).delete(1L);

        mockMvc.perform(delete("/api/resenas/resena/1")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk());

        verify(resenaService).delete(1L);
    }

    @Test
    void create_ShouldReturnForbidden_WhenNoToken() throws Exception {
        mockMvc.perform(post("/api/resenas/resena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resenaRequest)))
                .andExpect(status().isForbidden());

        verify(resenaService, never()).create(any());
    }

    @Test
    void create_ShouldReturnError_WhenUsuarioNotFound() throws Exception {
        when(resenaService.create(any(ResenaRequest.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(post("/api/resenas/resena")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resenaRequest)))
                .andExpect(status().is5xxServerError());

        verify(resenaService).create(any(ResenaRequest.class));
    }

    @Test
    void create_ShouldReturnError_WhenPeliculaNotFound() throws Exception {
        when(resenaService.create(any(ResenaRequest.class)))
                .thenThrow(new RuntimeException("Película no encontrada"));

        mockMvc.perform(post("/api/resenas/resena")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resenaRequest)))
                .andExpect(status().is5xxServerError());

        verify(resenaService).create(any(ResenaRequest.class));
    }
}