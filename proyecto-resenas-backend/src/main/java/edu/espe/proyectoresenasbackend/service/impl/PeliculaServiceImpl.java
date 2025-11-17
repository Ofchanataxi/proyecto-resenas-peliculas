package edu.espe.proyectoresenasbackend.service.impl;

import edu.espe.proyectoresenasbackend.domain.Pelicula;
import edu.espe.proyectoresenasbackend.dto.PeliculaRequest;
import edu.espe.proyectoresenasbackend.dto.PeliculaResponse;
import edu.espe.proyectoresenasbackend.repository.PeliculaRepository;
import edu.espe.proyectoresenasbackend.service.PeliculaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeliculaServiceImpl implements PeliculaService {
    private final PeliculaRepository repository;

    public PeliculaServiceImpl(PeliculaRepository repository) {
        this.repository = repository;
    }

    @Override
    public PeliculaResponse create(PeliculaRequest request) {
        Pelicula p = toEntity(request);
        repository.save(p);
        return toResponse(p);
    }

    @Override
    public PeliculaResponse get(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));
    }

    @Override
    public List<PeliculaResponse> list() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PeliculaResponse update(Long id, PeliculaRequest request) {
        Pelicula p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));

        p.setTitulo(request.getTitulo());
        p.setDirector(request.getDirector());
        p.setGenero(request.getGenero());
        p.setDuracionMinutos(request.getDuracionMinutos());
        p.setFechaEstreno(request.getFechaEstreno());

        repository.save(p);

        return toResponse(p);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ---- MAPPERS ----

    private Pelicula toEntity(PeliculaRequest request) {
        Pelicula p = new Pelicula();
        p.setTitulo(request.getTitulo());
        p.setDirector(request.getDirector());
        p.setGenero(request.getGenero());
        p.setDuracionMinutos(request.getDuracionMinutos());
        p.setFechaEstreno(request.getFechaEstreno());
        return p;
    }

    private PeliculaResponse toResponse(Pelicula p) {
        PeliculaResponse r = new PeliculaResponse();
        r.setId(p.getId());
        r.setTitulo(p.getTitulo());
        r.setDirector(p.getDirector());
        r.setGenero(p.getGenero());
        r.setDuracionMinutos(p.getDuracionMinutos());
        r.setFechaEstreno(p.getFechaEstreno());
        return r;
    }
}
