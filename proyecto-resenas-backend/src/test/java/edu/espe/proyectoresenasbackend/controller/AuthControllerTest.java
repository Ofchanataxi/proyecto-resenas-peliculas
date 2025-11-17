package edu.espe.proyectoresenasbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.espe.proyectoresenasbackend.domain.Usuario;
import edu.espe.proyectoresenasbackend.dto.AuthResponse;
import edu.espe.proyectoresenasbackend.dto.LoginRequest;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioService usuarioService;

    private UsuarioRequestData usuarioRequest;
    private LoginRequest loginRequest;
    private Usuario usuario;
    private UsuarioResponse usuarioResponse;

    @BeforeEach
    void setUp() {

        usuarioRequest = new UsuarioRequestData();
        usuarioRequest.setNombreCompleto("Test User");
        usuarioRequest.setEmail("test@example.com");
        usuarioRequest.setContrasena("password123");
        usuarioRequest.setActivo(true);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setContrasena("password123");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreCompleto("Test User");
        usuario.setEmail("test@example.com");
        usuario.setContrasena("encodedPassword");
        usuario.setActivo(true);

        usuarioResponse = new UsuarioResponse();
        usuarioResponse.setId(1L);
        usuarioResponse.setNombreCompleto("Test User");
        usuarioResponse.setEmail("test@example.com");
        usuarioResponse.setActivo(true);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("pass")
                .roles("USER")
                .build();

        when(userDetailsService.loadUserByUsername("test@example.com"))
                .thenReturn(usuario);
    }

    @Test
    void register_ShouldReturnToken_WhenValidRequest() throws Exception {

        when(usuarioService.create(any())).thenReturn(usuarioResponse);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(usuario);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        mockMvc.perform(post("/api/resenas/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() throws Exception {

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(usuario);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        mockMvc.perform(post("/api/resenas/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_ShouldReturn500_WhenInvalidCredentials() throws Exception {

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/resenas/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError()); // <-- 500 REAL
    }

    @Test
    void login_ShouldReturnForbidden_WhenAccountDeactivated() throws Exception {

        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("User disabled"));

        mockMvc.perform(post("/api/resenas/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("ACCOUNT_DEACTIVATED"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void reactivateAccount_ShouldReactivate_WhenValidEmail() throws Exception {

        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(usuario);
        when(usuarioService.activate(anyLong())).thenReturn(usuarioResponse);

        mockMvc.perform(post("/api/resenas/auth/reactivate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void reactivateAccount_ShouldReturnBadRequest_WhenEmailMissing() throws Exception {
        mockMvc.perform(post("/api/resenas/auth/reactivate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getMyProfile_ShouldReturnUserProfile_WhenAuthenticated() throws Exception {

        Authentication fakeAuth = mock(Authentication.class);
        when(fakeAuth.isAuthenticated()).thenReturn(true);
        when(fakeAuth.getName()).thenReturn("test@example.com");

        when(userDetailsService.loadUserByUsername("test@example.com"))
                .thenReturn(usuario);

        mockMvc.perform(get("/api/resenas/auth/me")
                        .principal(fakeAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nombreCompleto").value("Test User"));
    }

    @Test
    void getMyProfile_ShouldReturn500_WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/api/resenas/auth/me"))
                .andExpect(status().isInternalServerError()); // <-- 500 REAL
    }
}
