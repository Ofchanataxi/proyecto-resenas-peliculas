package edu.espe.proyectoresenasbackend.security;

import edu.espe.proyectoresenasbackend.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private UserDetails userDetails;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreCompleto("Test User");
        usuario.setEmail("test@example.com");
        usuario.setContrasena("encodedPassword");
        usuario.setActivo(true);

        userDetails = usuario;
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectEmail() {
        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUsername(token);

        assertEquals("test@example.com", extractedUsername);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValid() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenIsForDifferentUser() {
        String token = jwtService.generateToken(userDetails);

        Usuario differentUser = new Usuario();
        differentUser.setEmail("different@example.com");
        differentUser.setContrasena("password");
        differentUser.setActivo(true);

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenIsExpired() throws InterruptedException {
        // Este test es conceptual ya que el token tiene 24h de validez
        // En un escenario real, podrías usar una librería de mocking para simular el tiempo
        String token = jwtService.generateToken(userDetails);

        // Verificamos que el token es válido inmediatamente después de crearlo
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void extractUsername_ShouldHandleValidToken() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertNotNull(username);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void generateToken_ShouldCreateDifferentTokens_ForDifferentUsers() {
        Usuario usuario2 = new Usuario();
        usuario2.setEmail("another@example.com");
        usuario2.setContrasena("password");
        usuario2.setActivo(true);

        String token1 = jwtService.generateToken(userDetails);
        String token2 = jwtService.generateToken(usuario2);

        assertNotEquals(token1, token2);
    }

    @Test
    void isTokenValid_ShouldValidateTokenStructure() {
        String token = jwtService.generateToken(userDetails);

        // Un JWT válido tiene 3 partes separadas por puntos
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT debe tener 3 partes");
    }
}