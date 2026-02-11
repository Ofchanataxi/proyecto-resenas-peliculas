// ...existing code...
package edu.espe.proyectoresenasbackend.service;

import edu.espe.proyectoresenasbackend.domain.Usuario;
import edu.espe.proyectoresenasbackend.dto.ChatMessageDTO;
import edu.espe.proyectoresenasbackend.dto.ChatMessageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;



/**
 * Interfaz para las operaciones de chat (mensajes, stream y utilidades del simulador)
 */
public interface ChatService {

    Mono<ChatMessageDTO> sendMessage(ChatMessageRequest request);

    Flux<ChatMessageDTO> streamMessages();

    Mono<List<ChatMessageDTO>> getRecentMessages();

    // Métodos usados por el simulador
    void emitSimulatedMessage(Usuario usuario, String contenido);

    Mono<Void> deleteSimulatedMessages();
}

