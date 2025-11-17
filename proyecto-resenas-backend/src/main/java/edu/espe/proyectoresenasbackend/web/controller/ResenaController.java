package edu.espe.proyectoresenasbackend.web.controller;

import edu.espe.proyectoresenasbackend.dto.ResenaRequest;
import edu.espe.proyectoresenasbackend.dto.ResenaResponse;
import edu.espe.proyectoresenasbackend.service.ResenaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {
    private final ResenaService service;

    public ResenaController(ResenaService service) {
        this.service = service;
    }

    @PostMapping
    public ResenaResponse create(@RequestBody ResenaRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ResenaResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<ResenaResponse> list() {
        return service.list();
    }

    @PutMapping("/{id}")
    public ResenaResponse update(@PathVariable Long id, @RequestBody ResenaRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
