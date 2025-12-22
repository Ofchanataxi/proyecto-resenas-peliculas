package edu.espe.proyectoresenasbackend.service.impl;

import edu.espe.proyectoresenasbackend.domain.Usuario;
import edu.espe.proyectoresenasbackend.repository.UsuarioRepository;
import edu.espe.proyectoresenasbackend.service.ChatSimulatorService;
import edu.espe.proyectoresenasbackend.service.ChatService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ChatSimulatorServiceImpl implements ChatSimulatorService {

    private final UsuarioRepository usuarioRepository;
    private final ChatService chatService;
    private final PasswordEncoder passwordEncoder;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private Disposable simulationSubscription;
    private final List<Long> simulatedUserIds = new ArrayList<>();

    private static final String[] NOMBRES_SIMULADOS = {
            "Bot Carlos", "Bot María", "Bot Juan", "Bot Ana", "Bot Pedro",
            "Bot Sofía", "Bot Diego", "Bot Lucía", "Bot Miguel", "Bot Elena"
    };

    private static final String[] MENSAJES_SIMULADOS = {
            "¿Alguien ha visto la nueva película de Marvel?",
            "Me encantó la última de Christopher Nolan",
            "¿Recomiendan alguna película de terror?",
            "La cinematografía de Blade Runner 2049 es increíble",
            "¿Qué opinan de las películas de Tarantino?",
            "Acabo de ver Inception por quinta vez y sigue siendo genial",
            "Las películas de Studio Ghibli son obras maestras",
            "¿Alguien más espera la secuela de Dune?",
            "El cine de los 80s tenía algo especial",
            "¿Cuál es su director favorito?",
            "Las películas clásicas nunca pasan de moda",
            "¿Han visto la nueva de Scorsese?",
            "El cine independiente merece más reconocimiento",
            "¿Qué piensan del cine europeo?",
            "Las películas animadas no son solo para niños",
            "El soundtrack puede hacer o deshacer una película",
            "¿Cine de acción o drama?",
            "Las películas en blanco y negro tienen su encanto",
            "¿Alguien más colecciona películas en físico?",
            "El cine es la mejor forma de arte"
    };

    public ChatSimulatorServiceImpl(UsuarioRepository usuarioRepository,
                                ChatService chatService,
                                PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.chatService = chatService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Inicia la simulación de chat
     */
    @Transactional
    public synchronized Map<String, Object> startSimulation() {
        if (isRunning.get()) {
            return Map.of(
                    "success", false,
                    "message", "La simulación ya está en ejecución",
                    "activeUsers", simulatedUserIds.size()
            );
        }

        try {
            // 1. Crear usuarios simulados
            createSimulatedUsers();

            // 2. Iniciar el flujo de mensajes
            simulationSubscription = Flux.interval(Duration.ofSeconds(5), Duration.ofSeconds(8))
                    .subscribe(tick -> sendRandomMessage());

            isRunning.set(true);

            return Map.of(
                    "success", true,
                    "message", "Simulación iniciada exitosamente",
                    "activeUsers", simulatedUserIds.size(),
                    "userNames", getNombresUsuariosSimulados()
            );
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "Error al iniciar simulación: " + e.getMessage()
            );
        }
    }

    /**
     * Detiene la simulación y elimina usuarios simulados
     */
    @Transactional
    public synchronized Map<String, Object> stopSimulation() {
        if (!isRunning.get()) {
            return Map.of(
                    "success", false,
                    "message", "No hay simulación en ejecución"
            );
        }

        try {
            // 1. Detener el flujo de mensajes
            if (simulationSubscription != null && !simulationSubscription.isDisposed()) {
                simulationSubscription.dispose();
            }

            // 2. Eliminar mensajes simulados
            chatService.deleteSimulatedMessages().block();

            // 3. Eliminar usuarios simulados
            deleteSimulatedUsers();

            isRunning.set(false);

            return Map.of(
                    "success", true,
                    "message", "Simulación detenida y datos limpiados exitosamente",
                    "deletedUsers", simulatedUserIds.size()
            );
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "Error al detener simulación: " + e.getMessage()
            );
        }
    }

    /**
     * Obtiene el estado actual de la simulación
     */
    public Map<String, Object> getSimulationStatus() {
        return Map.of(
                "isRunning", isRunning.get(),
                "activeUsers", simulatedUserIds.size(),
                "userNames", getNombresUsuariosSimulados()
        );
    }

    private void createSimulatedUsers() {
        simulatedUserIds.clear();

        for (String nombre : NOMBRES_SIMULADOS) {
            // Verificar si ya existe
            if (usuarioRepository.existsByEmail(nombre.toLowerCase().replace(" ", ".") + "@bot.sim")) {
                continue;
            }

            Usuario usuario = new Usuario();
            usuario.setNombreCompleto(nombre);
            usuario.setEmail(nombre.toLowerCase().replace(" ", ".") + "@bot.sim");
            usuario.setContrasena(passwordEncoder.encode("botpass123"));
            usuario.setActivo(true);

            Usuario saved = usuarioRepository.save(usuario);
            simulatedUserIds.add(saved.getId());
        }
    }

    private void deleteSimulatedUsers() {
        for (Long userId : simulatedUserIds) {
            usuarioRepository.deleteById(userId);
        }
        simulatedUserIds.clear();
    }

    private void sendRandomMessage() {
        if (simulatedUserIds.isEmpty()) {
            return;
        }

        try {
            // Seleccionar usuario aleatorio
            Long randomUserId = simulatedUserIds.get(
                    new Random().nextInt(simulatedUserIds.size())
            );

            Usuario usuario = usuarioRepository.findById(randomUserId)
                    .orElse(null);

            if (usuario != null) {
                // Seleccionar mensaje aleatorio
                String randomMessage = MENSAJES_SIMULADOS[
                        new Random().nextInt(MENSAJES_SIMULADOS.length)
                        ];

                chatService.emitSimulatedMessage(usuario, randomMessage);
            }
        } catch (Exception e) {
            System.err.println("Error enviando mensaje simulado: " + e.getMessage());
        }
    }

    private List<String> getNombresUsuariosSimulados() {
        return simulatedUserIds.stream()
                .map(id -> usuarioRepository.findById(id)
                        .map(Usuario::getNombreCompleto)
                        .orElse("Usuario desconocido"))
                .toList();
    }
}

