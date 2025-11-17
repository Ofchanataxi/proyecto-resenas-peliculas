package edu.espe.proyectoresenasbackend.service;

import edu.espe.proyectoresenasbackend.dto.ResenaRequest;
import edu.espe.proyectoresenasbackend.dto.ResenaResponse;

import java.util.List;

//Servicio para la entidad Resena
public interface ResenaService {
    ResenaResponse create(ResenaRequest request);

    ResenaResponse get(Long id);

    List<ResenaResponse> list();

    ResenaResponse update(Long id, ResenaRequest request);

    void delete(Long id);

    List<ResenaResponse> listByPeliculaId(Long peliculaId);
}
