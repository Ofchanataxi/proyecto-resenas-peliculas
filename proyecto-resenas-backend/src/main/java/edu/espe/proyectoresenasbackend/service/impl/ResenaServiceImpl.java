package edu.espe.proyectoresenasbackend.service.impl;

import edu.espe.proyectoresenasbackend.domain.Pelicula;
import edu.espe.proyectoresenasbackend.domain.Resena;
import edu.espe.proyectoresenasbackend.domain.Usuario;
import edu.espe.proyectoresenasbackend.dto.ResenaRequest;
import edu.espe.proyectoresenasbackend.dto.ResenaResponse;
import edu.espe.proyectoresenasbackend.repository.PeliculaRepository;
import edu.espe.proyectoresenasbackend.repository.ResenaRepository;
import edu.espe.proyectoresenasbackend.repository.UsuarioRepository;
import edu.espe.proyectoresenasbackend.service.ResenaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResenaServiceImpl implements ResenaService {
    private final ResenaRepository repository;
    private final UsuarioRepository usuarioRepo;
    private final PeliculaRepository peliculaRepo;

    public ResenaServiceImpl(ResenaRepository repository, UsuarioRepository usuarioRepo, PeliculaRepository peliculaRepo) {
        this.repository = repository;
        this.usuarioRepo = usuarioRepo;
        this.peliculaRepo = peliculaRepo;
    }

    @Override
    public ResenaResponse create(ResenaRequest request) {

        Usuario usuario = usuarioRepo.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pelicula pelicula = peliculaRepo.findById(request.getPeliculaId())
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));

        Resena r = new Resena();
        r.setComentario(request.getComentario());
        r.setCalificacion(request.getCalificacion());
        r.setUsuario(usuario);
        r.setPelicula(pelicula);

        repository.save(r);
        return toResponse(r);
    }

    @Override
    public ResenaResponse get(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));
    }

    @Override
    public List<ResenaResponse> list() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ResenaResponse update(Long id, ResenaRequest request) {
        Resena r = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));

        Usuario usuario = usuarioRepo.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pelicula pelicula = peliculaRepo.findById(request.getPeliculaId())
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));

        r.setComentario(request.getComentario());
        r.setCalificacion(request.getCalificacion());
        r.setUsuario(usuario);
        r.setPelicula(pelicula);

        repository.save(r);

        return toResponse(r);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ---- MAPPER ----
    private ResenaResponse toResponse(Resena r) {
        ResenaResponse resp = new ResenaResponse();
        resp.setId(r.getId());
        resp.setComentario(r.getComentario());
        resp.setCalificacion(r.getCalificacion());
        resp.setFechaCreacion(r.getFechaCreacion());
        resp.setUsuarioId(r.getUsuario().getId());
        resp.setPeliculaId(r.getPelicula().getId());
        return resp;
    }
}
