package edu.espe.proyectoresenasbackend.web.controller;

import edu.espe.proyectoresenasbackend.dto.CineRequest;
import edu.espe.proyectoresenasbackend.dto.CineResponse;
import edu.espe.proyectoresenasbackend.service.CineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cines")
public class CineController {
    private final CineService service;

    public CineController(CineService service) {
        this.service = service;
    }

    @PostMapping
    public CineResponse create(@RequestBody CineRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public CineResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<CineResponse> list() {
        return service.list();
    }

    @PutMapping("/{id}")
    public CineResponse update(@PathVariable Long id, @RequestBody CineRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
