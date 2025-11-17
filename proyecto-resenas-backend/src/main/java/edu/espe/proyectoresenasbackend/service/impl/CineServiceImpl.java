package edu.espe.proyectoresenasbackend.service.impl;

import edu.espe.proyectoresenasbackend.domain.Cine;
import edu.espe.proyectoresenasbackend.dto.CineRequest;
import edu.espe.proyectoresenasbackend.dto.CineResponse;
import edu.espe.proyectoresenasbackend.repository.CineRepository;
import edu.espe.proyectoresenasbackend.service.CineService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CineServiceImpl implements CineService {
    private final CineRepository repository;

    public CineServiceImpl(CineRepository repository) {
        this.repository = repository;
    }

    @Override
    public CineResponse create(CineRequest request) {
        Cine c = toEntity(request);
        repository.save(c);
        return toResponse(c);
    }

    @Override
    public CineResponse get(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Cine no encontrado"));
    }

    @Override
    public List<CineResponse> list() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CineResponse update(Long id, CineRequest request) {
        Cine c = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cine no encontrado"));

        c.setNombre(request.getNombre());
        c.setDireccion(request.getDireccion());
        c.setCiudad(request.getCiudad());

        repository.save(c);

        return toResponse(c);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ---- MAPPERS ----

    private Cine toEntity(CineRequest request) {
        Cine c = new Cine();
        c.setNombre(request.getNombre());
        c.setDireccion(request.getDireccion());
        c.setCiudad(request.getCiudad());
        return c;
    }

    private CineResponse toResponse(Cine c) {
        CineResponse r = new CineResponse();
        r.setId(c.getId());
        r.setNombre(c.getNombre());
        r.setDireccion(c.getDireccion());
        r.setCiudad(c.getCiudad());
        return r;
    }
}
