package edu.espe.proyectoresenasbackend.repository;

import edu.espe.proyectoresenasbackend.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findTop50ByOrderByFechaEnvioDesc();

    List<ChatMessage> findByEsSimuladoTrue();

    @Query("SELECT m FROM ChatMessage m WHERE m.fechaEnvio >= :desde ORDER BY m.fechaEnvio ASC")
    List<ChatMessage> findMessagesAfter(LocalDateTime desde);

    void deleteByEsSimuladoTrue();
}