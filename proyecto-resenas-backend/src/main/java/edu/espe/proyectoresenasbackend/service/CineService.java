package edu.espe.proyectoresenasbackend.service;

import edu.espe.proyectoresenasbackend.dto.CineRequest;
import edu.espe.proyectoresenasbackend.dto.CineResponse;

import java.util.List;

public interface CineService {

    CineResponse create(CineRequest request);

    CineResponse get(Long id);

    List<CineResponse> list();

    CineResponse update(Long id, CineRequest request);

    void delete(Long id);
}
