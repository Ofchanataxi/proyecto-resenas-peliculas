package edu.espe.proyectoresenasbackend.service;

import edu.espe.proyectoresenasbackend.dto.UsuarioRequestData;
import edu.espe.proyectoresenasbackend.dto.UsuarioResponse;

import java.util.List;

public interface UsuarioService {
    void delete(Long id);
    UsuarioResponse create(UsuarioRequestData request);
    UsuarioResponse getById(Long id);
    List<UsuarioResponse> list();
    UsuarioResponse deactivate(Long id);
    UsuarioResponse activate(Long id);
    UsuarioResponse update(Long id, UsuarioRequestData request);
    Object getStats();
}
