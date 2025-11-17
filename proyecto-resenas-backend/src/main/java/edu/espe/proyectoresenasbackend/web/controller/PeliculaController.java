package edu.espe.proyectoresenasbackend.web.controller;

import edu.espe.proyectoresenasbackend.model.Pelicula; // <-- Asegúrate de importar tu modelo Pelicula
import edu.espe.proyectoresenasbackend.service.PeliculaService; // <-- Necesitarás crear este servicio
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peliculas") // <-- Esta es la URL base para todas las películas
public class PeliculaController {

    // Inyectamos un servicio que manejará la lógica (debes crearlo)
    private final PeliculaService peliculaService;

    public PeliculaController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    // GET /api/peliculas (Público)
    // Este es el endpoint que tu HomePage está intentando llamar
    @GetMapping
    public ResponseEntity<List<Pelicula>> getAllPeliculas() {
        return ResponseEntity.ok(peliculaService.getAllPeliculas());
    }

    // GET /api/peliculas/{id} (Público)
    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> getPeliculaById(@PathVariable Long id) {
        Pelicula pelicula = peliculaService.getPeliculaById(id);
        return ResponseEntity.ok(pelicula);
    }

    // POST /api/peliculas (Protegido - Asumimos que solo admin puede crear)
    @PostMapping
    public ResponseEntity<Pelicula> createPelicula(@RequestBody Pelicula pelicula) {
        Pelicula nuevaPelicula = peliculaService.createPelicula(pelicula);
        return ResponseEntity.status(201).body(nuevaPelicula);
    }

    // PUT /api/peliculas/{id} (Protegido)
    @PutMapping("/{id}")
    public ResponseEntity<Pelicula> updatePelicula(@PathVariable Long id, @RequestBody Pelicula peliculaDetails) {
        Pelicula updatedPelicula = peliculaService.updatePelicula(id, peliculaDetails);
        return ResponseEntity.ok(updatedPelicula);
    }

    // DELETE /api/peliculas/{id} (Protegido)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePelicula(@PathVariable Long id) {
        peliculaService.deletePelicula(id);
        return ResponseEntity.noContent().build();
    }
}