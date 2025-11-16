package edu.espe.proyectoresenasbackend.web.controller;

import edu.espe.proyectoresenasbackend.dto.AuthResponse;
import edu.espe.proyectoresenasbackend.dto.LoginRequest;
import edu.espe.proyectoresenasbackend.dto.UsuarioRequestData;
import edu.espe.proyectoresenasbackend.security.JwtService;
import edu.espe.proyectoresenasbackend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/resenas/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtService jwtService, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    // Endpoint de Registro
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UsuarioRequestData request) {
        // 1. Crea el usuario
        usuarioService.create(request);
        // 2. Carga los detalles y genera un token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String token = jwtService.generateToken(userDetails);
        // 3. Devuelve el token
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
    }

    // Endpoint de Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // 1. Intenta autenticar
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getContrasena())
            );

            // 2. Si tiene éxito, genera y devuelve el token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            final String token = jwtService.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (BadCredentialsException e) {
            // 3. Si la contraseña es incorrecta
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos");

            // --- LÓGICA DE REACTIVACIÓN ---
        } catch (DisabledException e) {
            // 4. Si la cuenta está desactivada (isEnabled() devolvió false)
            // Devuelve un error 403 (Prohibido) con un cuerpo JSON específico
            Map<String, String> errorResponse = Map.of(
                    "error", "ACCOUNT_DEACTIVATED",
                    "message", "Tu cuenta está desactivada. ¿Deseas reactivarla?",
                    "email", request.getEmail() // Devuelve el email para que el front sepa a quién reactivar
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
        // --- FIN LÓGICA DE REACTIVACIÓN ---
    }

    // Endpoint público para reactivar la cuenta
    // El front llamará a este si recibe el error "ACCOUNT_DEACTIVATED"
    @PostMapping("/reactivate")
    public ResponseEntity<?> reactivateAccount(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null) {
            return ResponseEntity.badRequest().body("Email requerido");
        }

        // Usamos el UserDetailsService para cargar el Usuario
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        // (Asumimos que es una instancia de nuestro Usuario, lo cual es seguro aquí)
        edu.espe.proyectoresenasbackend.domain.Usuario usuario = (edu.espe.proyectoresenasbackend.domain.Usuario) userDetails;

        // Llama a tu servicio existente para activar
        usuarioService.activate(usuario.getId());

        return ResponseEntity.ok(Map.of("message", "Cuenta reactivada exitosamente. Por favor, inicia sesión."));
    }
}