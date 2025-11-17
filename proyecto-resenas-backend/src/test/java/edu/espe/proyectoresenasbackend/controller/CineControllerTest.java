package edu.espe.proyectoresenasbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.espe.proyectoresenasbackend.dto.CineRequest;
import edu.espe.proyectoresenasbackend.dto.CineResponse;
import edu.espe.proyectoresenasbackend.security.JwtService;
import edu.espe.proyectoresenasbackend.service.CineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
        import static org.mockito.Mockito.*;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CineService cineService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private CineRequest cineRequest;
    private CineResponse cineResponse;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        cineRequest = new CineRequest();
        cineRequest.setNombre("Cineplex Quito");
        cineRequest.setDireccion("Av. Amazonas y Naciones Unidas");
        cineRequest.setCiudad("Quito");

        cineResponse = new CineResponse();
        cineResponse.setId(1L);
        cineResponse.setNombre("Cineplex Quito");
        cineResponse.setDireccion("Av. Amazonas y Naciones Unidas");
        cineResponse.setCiudad("Quito");

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
    void create_ShouldReturnCineResponse_WhenValidRequest() throws Exception {
        when(cineService.create(any(CineRequest.class))).thenReturn(cineResponse);

        mockMvc.perform(post("/api/resenas/cines")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cineRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Cineplex Quito"))
                .andExpect(jsonPath("$.ciudad").value("Quito"));

        verify(cineService).create(any(CineRequest.class));
    }

    @Test
    void get_ShouldReturnCine_WhenIdExists() throws Exception {
        when(cineService.get(1L)).thenReturn(cineResponse);

        mockMvc.perform(get("/api/resenas/cines/1")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Cineplex Quito"));

        verify(cineService).get(1L);
    }

    @Test
    void list_ShouldReturnAllCines() throws Exception {
        CineResponse cine2 = new CineResponse();
        cine2.setId(2L);
        cine2.setNombre("Multicines");
        cine2.setDireccion("Centro Comercial El Recreo");
        cine2.setCiudad("Quito");

        List<CineResponse> cines = Arrays.asList(cineResponse, cine2);
        when(cineService.list()).thenReturn(cines);

        mockMvc.perform(get("/api/resenas/cines")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Cineplex Quito"))
                .andExpect(jsonPath("$[1].nombre").value("Multicines"));

        verify(cineService).list();
    }

    @Test
    void update_ShouldReturnUpdatedCine_WhenValidRequest() throws Exception {
        CineResponse updatedCine = new CineResponse();
        updatedCine.setId(1L);
        updatedCine.setNombre("Cineplex Quito - Norte");
        updatedCine.setDireccion("Av. Amazonas y Naciones Unidas");
        updatedCine.setCiudad("Quito");

        when(cineService.update(eq(1L), any(CineRequest.class))).thenReturn(updatedCine);

        mockMvc.perform(put("/api/resenas/cines/1")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cineRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cineplex Quito - Norte"));

        verify(cineService).update(eq(1L), any(CineRequest.class));
    }

    @Test
    void delete_ShouldCallService_WhenValidId() throws Exception {
        doNothing().when(cineService).delete(1L);

        mockMvc.perform(delete("/api/resenas/cines/1")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk());

        verify(cineService).delete(1L);
    }

    @Test
    void create_ShouldReturnForbidden_WhenNoToken() throws Exception {
        mockMvc.perform(post("/api/resenas/cines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cineRequest)))
                .andExpect(status().isForbidden());

        verify(cineService, never()).create(any());
    }
}