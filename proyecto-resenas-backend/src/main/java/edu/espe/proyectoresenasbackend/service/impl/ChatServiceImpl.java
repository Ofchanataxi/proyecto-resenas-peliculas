package edu.espe.proyectoresenasbackend.service.impl;

import edu.espe.proyectoresenasbackend.domain.ChatMessage;
import edu.espe.proyectoresenasbackend.domain.Usuario;
import edu.espe.proyectoresenasbackend.dto.ChatMessageDTO;
import edu.espe.proyectoresenasbackend.dto.ChatMessageRequest;
import edu.espe.proyectoresenasbackend.repository.ChatMessageRepository;
import edu.espe.proyectoresenasbackend.repository.UsuarioRepository;
import edu.espe.proyectoresenasbackend.service.ChatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UsuarioRepository usuarioRepository;

    // Sink para broadcast de mensajes a todos los clientes conectados
    private final Sinks.Many<ChatMessageDTO> messageSink;

    public ChatServiceImpl(ChatMessageRepository chatMessageRepository, UsuarioRepository usuarioRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.usuarioRepository = usuarioRepository;

        // Multicast permite múltiples suscriptores
        this.messageSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    /**
     * Envía un mensaje al chat
     */
    @Transactional
    public Mono<ChatMessageDTO> sendMessage(ChatMessageRequest request) {
        return Mono.fromCallable(() -> {
            Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            ChatMessage message = new ChatMessage();
            message.setUsuario(usuario);
            message.setContenido(request.getContenido());
            message.setEsSimulado(false);

            ChatMessage saved = chatMessageRepository.save(message);
            ChatMessageDTO dto = toDTO(saved);

            // Emitir el mensaje a todos los suscriptores
            messageSink.tryEmitNext(dto);

            return dto;
        });
    }

    /**
     * Stream de mensajes en tiempo real (SSE - Server-Sent Events)
     */
    public Flux<ChatMessageDTO> streamMessages() {
        return messageSink.asFlux()
                .onBackpressureBuffer(100); // Buffer de 100 mensajes
    }

    /**
     * Obtiene los últimos 50 mensajes (para carga inicial)
     */
    public Mono<List<ChatMessageDTO>> getRecentMessages() {
        return Mono.fromCallable(() ->
                chatMessageRepository.findTop50ByOrderByFechaEnvioDesc()
                        .stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Emite un mensaje desde el simulador (uso interno)
     */
    @Transactional
    public void emitSimulatedMessage(Usuario usuario, String contenido) {
        ChatMessage message = new ChatMessage();
        message.setUsuario(usuario);
        message.setContenido(contenido);
        message.setEsSimulado(true);

        ChatMessage saved = chatMessageRepository.save(message);
        ChatMessageDTO dto = toDTO(saved);

        messageSink.tryEmitNext(dto);
    }

    /**
     * Elimina todos los mensajes simulados
     */
    @Transactional
    public Mono<Void> deleteSimulatedMessages() {
        return Mono.fromRunnable(() -> chatMessageRepository.deleteByEsSimuladoTrue());
    }

    private ChatMessageDTO toDTO(ChatMessage message) {
        return new ChatMessageDTO(
                message.getId(),
                message.getUsuario().getId(),
                message.getUsuario().getNombreCompleto(),
                message.getContenido(),
                message.getFechaEnvio(),
                message.getEsSimulado()
        );
    }
}