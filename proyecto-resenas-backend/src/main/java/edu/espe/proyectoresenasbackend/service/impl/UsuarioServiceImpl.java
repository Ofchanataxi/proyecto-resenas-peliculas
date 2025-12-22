
package edu.espe.proyectoresenasbackend.service.impl;

import edu.espe.proyectoresenasbackend.domain.Usuario;
import edu.espe.proyectoresenasbackend.dto.UsuarioRequestData;
import edu.espe.proyectoresenasbackend.dto.UsuarioResponse;
import edu.espe.proyectoresenasbackend.repository.UsuarioRepository;
import edu.espe.proyectoresenasbackend.service.UsuarioService;
import edu.espe.proyectoresenasbackend.web.advice.ConflictException;
import edu.espe.proyectoresenasbackend.web.advice.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponse create(UsuarioRequestData request) {
        if(repo.existsByEmail(request.getEmail())){
            throw new ConflictException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEmail(request.getEmail());

        // Esta línea ahora es válida porque passwordEncoder está inicializado
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));

        usuario.setActivo(true);
        Usuario saved = repo.save(usuario);
        return toResponse(saved);
    }

    @Override
    public Usuario createUser(UsuarioRequestData request) {
        if(repo.existsByEmail(request.getEmail())){
            throw new ConflictException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEmail(request.getEmail());
        // Encriptar contraseña
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        usuario.setActivo(true);

        // Guardar y retornar la entidad completa (con el ID generado)
        return repo.save(usuario);
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

        if (request.getContrasena() != null && !request.getContrasena().isEmpty()) {
            // Esta línea también es válida ahora
            usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        }

        return toResponse(repo.save(usuario));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Usuario no encontrado");
        }
        repo.deleteById(id);
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