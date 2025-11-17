package edu.espe.proyectoresenasbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.espe.proyectoresenasbackend.dto.UsuarioRequestData;
import edu.espe.proyectoresenasbackend.dto.UsuarioResponse;
import edu.espe.proyectoresenasbackend.security.JwtService;
import edu.espe.proyectoresenasbackend.service.UsuarioService;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private UsuarioRequestData usuarioRequest;
    private UsuarioResponse usuarioResponse;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        usuarioRequest = new UsuarioRequestData();
        usuarioRequest.setNombreCompleto("Juan Pérez");
        usuarioRequest.setEmail("juan@example.com");
        usuarioRequest.setContrasena("password123");
        usuarioRequest.setActivo(true);

        usuarioResponse = new UsuarioResponse();
        usuarioResponse.setId(1L);
        usuarioResponse.setNombreCompleto("Juan Pérez");
        usuarioResponse.setEmail("juan@example.com");
        usuarioResponse.setActivo(true);

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
    void create_ShouldReturnUsuarioResponse_WhenValidRequest() throws Exception {
        when(usuarioService.create(any(UsuarioRequestData.class))).thenReturn(usuarioResponse);

        mockMvc.perform(post("/api/resenas/usuarios")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreCompleto").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan@example.com"))
                .andExpect(jsonPath("$.activo").value(true));

        verify(usuarioService).create(any(UsuarioRequestData.class));
    }

    @Test
    void getById_ShouldReturnUsuario_WhenIdExists() throws Exception {
        when(usuarioService.getById(1L)).thenReturn(usuarioResponse);

        mockMvc.perform(get("/api/resenas/usuarios/1")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreCompleto").value("Juan Pérez"));

        verify(usuarioService).getById(1L);
    }

    @Test
    void getAll_ShouldReturnAllUsuarios() throws Exception {
        UsuarioResponse usuario2 = new UsuarioResponse();
        usuario2.setId(2L);
        usuario2.setNombreCompleto("María García");
        usuario2.setEmail("maria@example.com");
        usuario2.setActivo(true);

        List<UsuarioResponse> usuarios = Arrays.asList(usuarioResponse, usuario2);
        when(usuarioService.list()).thenReturn(usuarios);

        mockMvc.perform(get("/api/resenas/usuarios")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombreCompleto").value("Juan Pérez"))
                .andExpect(jsonPath("$[1].nombreCompleto").value("María García"));

        verify(usuarioService).list();
    }

    @Test
    void update_ShouldReturnUpdatedUsuario_WhenValidRequest() throws Exception {
        UsuarioResponse updatedUsuario = new UsuarioResponse();
        updatedUsuario.setId(1L);
        updatedUsuario.setNombreCompleto("Juan Pérez Actualizado");
        updatedUsuario.setEmail("juan@example.com");
        updatedUsuario.setActivo(true);

        when(usuarioService.update(eq(1L), any(UsuarioRequestData.class))).thenReturn(updatedUsuario);

        mockMvc.perform(put("/api/resenas/usuarios/1")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Juan Pérez Actualizado"));

        verify(usuarioService).update(eq(1L), any(UsuarioRequestData.class));
    }

    @Test
    void deactivate_ShouldReturnDeactivatedUsuario() throws Exception {
        UsuarioResponse deactivatedUsuario = new UsuarioResponse();
        deactivatedUsuario.setId(1L);
        deactivatedUsuario.setNombreCompleto("Juan Pérez");
        deactivatedUsuario.setEmail("juan@example.com");
        deactivatedUsuario.setActivo(false);

        when(usuarioService.deactivate(1L)).thenReturn(deactivatedUsuario);

        mockMvc.perform(patch("/api/resenas/usuarios/1/deactivate")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));

        verify(usuarioService).deactivate(1L);
    }

    @Test
    void activate_ShouldReturnActivatedUsuario() throws Exception {
        UsuarioResponse activatedUsuario = new UsuarioResponse();
        activatedUsuario.setId(1L);
        activatedUsuario.setNombreCompleto("Juan Pérez");
        activatedUsuario.setEmail("juan@example.com");
        activatedUsuario.setActivo(true);

        when(usuarioService.activate(1L)).thenReturn(activatedUsuario);

        mockMvc.perform(patch("/api/resenas/usuarios/1/activate")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(true));

        verify(usuarioService).activate(1L);
    }

    @Test
    void getStatistics_ShouldReturnStats() throws Exception {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", 10L);
        stats.put("active", 8L);
        stats.put("inactive", 2L);

        when(usuarioService.getStats()).thenReturn(stats);

        mockMvc.perform(get("/api/resenas/usuarios/stats")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.active").value(8))
                .andExpect(jsonPath("$.inactive").value(2));

        verify(usuarioService).getStats();
    }

    @Test
    void delete_ShouldReturnNoContent_WhenValidId() throws Exception {
        doNothing().when(usuarioService).delete(1L);

        mockMvc.perform(delete("/api/resenas/usuarios/1")
                        .header("Authorization", jwtToken))
                .andExpect(status().isNoContent());

        verify(usuarioService).delete(1L);
    }

    @Test
    void create_ShouldReturnForbidden_WhenNoToken() throws Exception {
        mockMvc.perform(post("/api/resenas/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isForbidden());

        verify(usuarioService, never()).create(any());
    }
}