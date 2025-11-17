package edu.espe.proyectoresenasbackend.web.controller;

import edu.espe.proyectoresenasbackend.domain.Pelicula; // <-- Asegúrate de importar tu modelo Pelicula
import edu.espe.proyectoresenasbackend.dto.PeliculaRequest;
import edu.espe.proyectoresenasbackend.dto.PeliculaResponse;
import edu.espe.proyectoresenasbackend.service.PeliculaService; // <-- Necesitarás crear este servicio
import edu.espe.proyectoresenasbackend.service.PeliculaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Controlador REST para la entidad Pelicula
@RestController
@RequestMapping("/api/resenas/peliculas")
public class PeliculaController {
    private final PeliculaService service;

    public PeliculaController(PeliculaService service) {
        this.service = service;
    }

    @PostMapping
    public PeliculaResponse create(@RequestBody PeliculaRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public PeliculaResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<PeliculaResponse> list() {
        return service.list();
    }

    @PutMapping("/{id}")
    public PeliculaResponse update(@PathVariable Long id, @RequestBody PeliculaRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
