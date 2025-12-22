package edu.espe.proyectoresenasbackend.web.controller;

import edu.espe.proyectoresenasbackend.dto.ChatMessageDTO;
import edu.espe.proyectoresenasbackend.dto.ChatMessageRequest;
import edu.espe.proyectoresenasbackend.service.ChatService;
import edu.espe.proyectoresenasbackend.service.ChatSimulatorService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatSimulatorService chatSimulatorService;

    public ChatController(ChatService chatService, ChatSimulatorService chatSimulatorService) {
        this.chatService = chatService;
        this.chatSimulatorService = chatSimulatorService;
    }

    /**
     * Envía un mensaje al chat
     * POST /api/resenas/chat/messages
     */
    @PostMapping("/messages")
    public Mono<ResponseEntity<ChatMessageDTO>> sendMessage(@Valid @RequestBody ChatMessageRequest request) {
        return chatService.sendMessage(request)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    /**
     * Stream de mensajes en tiempo real usando Server-Sent Events (SSE)
     * GET /api/resenas/chat/stream
     *
     * Los clientes pueden conectarse a este endpoint para recibir mensajes en tiempo real
     * Ejemplo JavaScript:
     * const eventSource = new EventSource('/api/resenas/chat/stream');
     * eventSource.onmessage = (event) => console.log(JSON.parse(event.data));
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatMessageDTO> streamMessages() {
        return chatService.streamMessages();
    }

    /**
     * Obtiene los últimos 50 mensajes para carga inicial
     * GET /api/resenas/chat/messages/recent
     */
    @GetMapping("/messages/recent")
    public Mono<ResponseEntity<List<ChatMessageDTO>>> getRecentMessages() {
        return chatService.getRecentMessages()
                .map(ResponseEntity::ok);
    }

    /**
     * SIMULADOR: Inicia la simulación de chat con usuarios bot
     * POST /api/resenas/chat/simulator/start
     */
    @PostMapping("/simulator/start")
    public ResponseEntity<Map<String, Object>> startSimulation() {
        Map<String, Object> result = chatSimulatorService.startSimulation();
        return ResponseEntity.ok(result);
    }

    /**
     * SIMULADOR: Detiene la simulación y elimina usuarios bot
     * POST /api/resenas/chat/simulator/stop
     */
    @PostMapping("/simulator/stop")
    public ResponseEntity<Map<String, Object>> stopSimulation() {
        Map<String, Object> result = chatSimulatorService.stopSimulation();
        return ResponseEntity.ok(result);
    }

    /**
     * SIMULADOR: Obtiene el estado actual de la simulación
     * GET /api/resenas/chat/simulator/status
     */
    @GetMapping("/simulator/status")
    public ResponseEntity<Map<String, Object>> getSimulatorStatus() {
        Map<String, Object> status = chatSimulatorService.getSimulationStatus();
        return ResponseEntity.ok(status);
    }
}