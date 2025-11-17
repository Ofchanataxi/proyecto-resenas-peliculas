package edu.espe.proyectoresenasbackend.web.controller;

import edu.espe.proyectoresenasbackend.dto.UsuarioRequestData;
import edu.espe.proyectoresenasbackend.dto.UsuarioResponse;
import edu.espe.proyectoresenasbackend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> create(@Valid @RequestBody UsuarioRequestData request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> getAll() {
        return ResponseEntity.ok(usuarioService.list());
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UsuarioResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.deactivate(id));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UsuarioResponse> activate(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.activate(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(@PathVariable Long id, @Valid @RequestBody UsuarioRequestData request) {
        return ResponseEntity.ok(usuarioService.update(id, request));
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStatistics() {
        return ResponseEntity.ok(usuarioService.getStats());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Devuelve 204 No Content si tiene éxito
    public void delete(@PathVariable Long id) {
        usuarioService.delete(id);
    }
}
