package edu.espe.proyectoresenasbackend.service.impl;

import edu.espe.proyectoresenasbackend.domain.Usuario;
import edu.espe.proyectoresenasbackend.dto.UsuarioRequestData;
import edu.espe.proyectoresenasbackend.dto.UsuarioResponse;
import edu.espe.proyectoresenasbackend.repository.UsuarioRepository;
import edu.espe.proyectoresenasbackend.service.UsuarioService;
import edu.espe.proyectoresenasbackend.web.advice.ConflictException;
import edu.espe.proyectoresenasbackend.web.advice.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder; // <-- Inyectar

    // Actualizar constructor
    public UsuarioServiceImpl(UsuarioRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder; // <-- Inyectar
    }

    @Override
    public UsuarioResponse create(UsuarioRequestData request) {
        if(repo.existsByEmail(request.getEmail())){
            throw new ConflictException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEmail(request.getEmail());

        // --- ¡CAMBIO IMPORTANTE! ---
        // Hashear la contraseña antes de guardarla
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        // --- FIN DEL CAMBIO ---

        usuario.setActivo(true);
        Usuario saved = repo.save(usuario);
        return toResponse(saved);
    }

    @Override
    public UsuarioResponse update(Long id, UsuarioRequestData request) {
        Usuario usuario = repo.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (!usuario.getEmail().equals(request.getEmail()) && repo.existsByEmail(request.getEmail())) {
            throw new ConflictException("El email ya está registrado");
        }

        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEmail(request.getEmail());
        usuario.setActivo(request.getActivo());

        // --- ¡CAMBIO IMPORTANTE! ---
        // Opcional: Actualizar contraseña solo si se provee una nueva
        if (request.getContrasena() != null && !request.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        }
        // --- FIN DEL CAMBIO ---

        return toResponse(repo.save(usuario));
    }

    // --- NUEVO MÉTODO DE ELIMINACIÓN ---
    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Usuario no encontrado");
        }
        repo.deleteById(id);
    }
    // --- FIN DEL NUEVO MÉTODO ---

    public UsuarioServiceImpl(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UsuarioResponse create(UsuarioRequestData request) {
        if(repo.existsByEmail(request.getEmail())){
            throw new ConflictException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEmail(request.getEmail());
        // Aquí deberías hashear la contraseña antes de guardarla
        // usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        usuario.setContrasena(request.getContrasena()); // ¡Temporal, no seguro!
        usuario.setActivo(true);

        Usuario saved = repo.save(usuario);
        return toResponse(saved);
    }

    @Override
    public UsuarioResponse getById(Long id) {
        Usuario usuario = repo.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return toResponse(usuario);
    }

    @Override
    public List<UsuarioResponse> list() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public UsuarioResponse deactivate(Long id) {
        Usuario usuario = repo.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        usuario.setActivo(false);
        return toResponse(repo.save(usuario));
    }

    @Override
    public UsuarioResponse activate(Long id) {
        Usuario usuario = repo.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        usuario.setActivo(true);
        return toResponse(repo.save(usuario));
    }

    @Override
    public UsuarioResponse update(Long id, UsuarioRequestData request) {
        Usuario usuario = repo.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        // Lógica para el email duplicado en actualización
        if (!usuario.getEmail().equals(request.getEmail()) && repo.existsByEmail(request.getEmail())) {
            throw new ConflictException("El email ya está registrado");
        }

        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEmail(request.getEmail());
        usuario.setActivo(request.getActivo());
        // Opcional: Actualizar contraseña si se provee una nueva
        if (request.getContrasena() != null && !request.getContrasena().isEmpty()) {
            // usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
            usuario.setContrasena(request.getContrasena()); // ¡Temporal!
        }

        return toResponse(repo.save(usuario));
    }

    @Override
    public Map<String, Long> getStats() {
        Long total = repo.count();
        Long active = repo.countByActivoTrue();
        Long inactive = repo.countByActivoFalse();

        return Map.of(
                "total", total,
                "active", active,
                "inactive", inactive
        );
    }

    private UsuarioResponse toResponse(Usuario usuario){
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNombreCompleto(usuario.getNombreCompleto());
        response.setEmail(usuario.getEmail());
        response.setActivo(usuario.getActivo());
        return response;
    }
}
