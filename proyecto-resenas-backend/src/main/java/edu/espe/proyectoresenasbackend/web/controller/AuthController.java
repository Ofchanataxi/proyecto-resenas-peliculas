package edu.espe.proyectoresenasbackend.web.controller;

import edu.espe.proyectoresenasbackend.domain.Usuario;
import edu.espe.proyectoresenasbackend.dto.AuthResponse;
import edu.espe.proyectoresenasbackend.dto.LoginRequest;
import edu.espe.proyectoresenasbackend.dto.UsuarioRequestData;
import edu.espe.proyectoresenasbackend.dto.UsuarioResponse;
import edu.espe.proyectoresenasbackend.security.JwtService;
import edu.espe.proyectoresenasbackend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

//Controlador REST para autenticación y registro
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
    // @Valid es importante aquí para validar los campos antes de entrar al código
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UsuarioRequestData request) {
        // Ahora sí funcionará esta llamada porque agregaste 'createUser'
        Usuario nuevoUsuario = usuarioService.createUser(request);

        String token = jwtService.generateToken(nuevoUsuario);

        AuthResponse response = new AuthResponse(
                token,
                nuevoUsuario.getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // --- ENDPOINT LOGIN CORREGIDO ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // 1. Autenticar
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getContrasena())
            );

            // 2. Cargar usuario y hacer CAST a nuestra entidad 'Usuario'
            // Esto funciona porque tu clase Usuario implementa UserDetails
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            Usuario usuario = (Usuario) userDetails;

            // 3. Generar Token
            String token = jwtService.generateToken(usuario);

            // 4. Devolver Token + ID + Nombre
            return ResponseEntity.ok(new AuthResponse(
                    token,
                    usuario.getId()
            ));

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos");
        } catch (DisabledException e) {
            Map<String, String> errorResponse = Map.of(
                    "error", "ACCOUNT_DEACTIVATED",
                    "message", "Tu cuenta está desactivada. ¿Deseas reactivarla?",
                    "email", request.getEmail()
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
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

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> getMyProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        // El "name" en el objeto Authentication es el email que guardamos en el token
        String userEmail = authentication.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        // Convertimos el UserDetails (que es nuestro Usuario) a un DTO de respuesta
        Usuario usuario = (Usuario) userDetails;

        // Reutilizamos el método toResponse de tu UsuarioServiceImpl
        // (Necesitarás inyectar UsuarioService en este controlador)
        // O simplemente creamos el DTO aquí:
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNombreCompleto(usuario.getNombreCompleto());
        response.setEmail(usuario.getEmail());
        response.setActivo(usuario.isEnabled());

        return ResponseEntity.ok(response);
    }
}