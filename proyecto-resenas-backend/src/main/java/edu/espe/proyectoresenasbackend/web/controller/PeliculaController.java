package edu.espe.proyectoresenasbackend.web.controller;

// 1. Importa los DTOs que vas a usar
import edu.espe.proyectoresenasbackend.dto.PeliculaRequest;
import edu.espe.proyectoresenasbackend.dto.PeliculaResponse;
import edu.espe.proyectoresenasbackend.service.PeliculaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peliculas") // Esta URL base es correcta
public class PeliculaController {

    private final PeliculaService peliculaService;

    public PeliculaController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    // GET /api/peliculas (Público)
    // 2. Cambia el tipo de retorno a List<PeliculaResponse>
    @GetMapping
    public ResponseEntity<List<PeliculaResponse>> getAllPeliculas() {
        // Asumimos que peliculaService.list() devuelve List<PeliculaResponse>
        return ResponseEntity.ok(peliculaService.list());
    }

    // GET /api/peliculas/{id} (Público)
    // 3. Cambia el tipo de retorno a PeliculaResponse
    @GetMapping("/{id}")
    public ResponseEntity<PeliculaResponse> getPeliculaById(@PathVariable Long id) {
        // 4. Cambia el nombre del método del servicio para que sea consistente (getById)
        // Asumimos que peliculaService.getById(id) devuelve PeliculaResponse
        PeliculaResponse pelicula = peliculaService.get(id);
        return ResponseEntity.ok(pelicula);
    }

    // POST /api/peliculas (Protegido)
    // 5. Cambia el parámetro a PeliculaRequest y el retorno a PeliculaResponse
    @PostMapping
    public ResponseEntity<PeliculaResponse> createPelicula(@RequestBody PeliculaRequest peliculaRequest) {
        // Asumimos que peliculaService.create() acepta un Request y devuelve un Response
        PeliculaResponse nuevaPelicula = peliculaService.create(peliculaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPelicula);
    }

    // PUT /api/peliculas/{id} (Protegido)
    // 6. Cambia el parámetro a PeliculaRequest y el retorno a PeliculaResponse
    @PutMapping("/{id}")
    public ResponseEntity<PeliculaResponse> updatePelicula(@PathVariable Long id, @RequestBody PeliculaRequest peliculaRequest) {
        // Asumimos que peliculaService.update() acepta ID y Request, y devuelve un Response
        PeliculaResponse updatedPelicula = peliculaService.update(id, peliculaRequest);
        return ResponseEntity.ok(updatedPelicula);
    }

    // DELETE /api/peliculas/{id} (Protegido)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePelicula(@PathVariable Long id) {
        peliculaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}