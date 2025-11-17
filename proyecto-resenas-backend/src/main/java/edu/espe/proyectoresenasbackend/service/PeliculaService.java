package edu.espe.proyectoresenasbackend.service;

import edu.espe.proyectoresenasbackend.dto.PeliculaRequest;
import edu.espe.proyectoresenasbackend.dto.PeliculaResponse;

import java.util.List;

public interface PeliculaService {
    PeliculaResponse create(PeliculaRequest request);

    PeliculaResponse get(Long id);

    List<PeliculaResponse> list();

    PeliculaResponse update(Long id, PeliculaRequest request);

    void delete(Long id);
}
