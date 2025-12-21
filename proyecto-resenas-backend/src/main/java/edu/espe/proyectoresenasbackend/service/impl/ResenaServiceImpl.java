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
import edu.espe.proyectoresenasbackend.util.CustomSubscriber; // Importar tu subscriber
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<ResenaResponse> create(ResenaRequest request) {
        return Mono.fromCallable(() -> {
            Usuario usuario = usuarioRepo.findById(request.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Pelicula pelicula = peliculaRepo.findById(request.getPeliculaId())
                    .orElseThrow(() -> new RuntimeException("Película no encontrada"));

            Resena r = new Resena();
            r.setComentario(request.getComentario());
            r.setCalificacion(request.getCalificacion());
            r.setUsuario(usuario);
            r.setPelicula(pelicula);

            return repository.save(r);
        }).map(this::toResponse);
    }

    @Override
    public Mono<ResenaResponse> get(Long id) {
        return Mono.fromCallable(() -> repository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Reseña no encontrada")))
                .map(this::toResponse);
    }

    @Override
    public Flux<ResenaResponse> list() {
        // Convertimos la lista bloqueante en un Flux
        return Flux.defer(() -> Flux.fromIterable(repository.findAll()))
                .map(this::toResponse);
    }

    @Override
    public Mono<ResenaResponse> update(Long id, ResenaRequest request) {
        return Mono.fromCallable(() -> {
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

            return repository.save(r);
        }).map(this::toResponse);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return Mono.fromRunnable(() -> repository.deleteById(id));
    }

    @Override
    public Flux<ResenaResponse> listByPeliculaId(Long peliculaId) {
        return Flux.defer(() -> Flux.fromIterable(repository.findByPeliculaId(peliculaId)))
                .map(this::toResponse);
    }

    // --- Implementación del Laboratorio (Backpressure) ---
    @Override
    public List<String> procesarCalificacionesPorLotes() {
        // 1. Crear el Flux
        Flux<Integer> calificacionesFlux = Flux.fromIterable(repository.findAll())
                .map(Resena::getCalificacion);

        // 2. Crear instancia de tu subscriber
        CustomSubscriber subscriber = new CustomSubscriber(3);

        // 3. Suscribirse (Como es un Flux de Iterable, esto ocurre síncronamente aquí)
        calificacionesFlux.subscribe(subscriber);

        // 4. Devolver los logs capturados
        return subscriber.getLogs();
    }

    private ResenaResponse toResponse(Resena r) {
        ResenaResponse resp = new ResenaResponse();
        resp.setId(r.getId());
        resp.setComentario(r.getComentario());
        resp.setCalificacion(r.getCalificacion());
        resp.setFechaCreacion(r.getFechaCreacion());

        // Mapeo del Usuario
        if (r.getUsuario() != null) {
            resp.setUsuarioId(r.getUsuario().getId());
            // AQUÍ AGREGAMOS EL NOMBRE
            resp.setUsuarioNombre(r.getUsuario().getNombreCompleto());
        }

        if (r.getPelicula() != null) {
            resp.setPeliculaId(r.getPelicula().getId());
        }
        return resp;
    }
}