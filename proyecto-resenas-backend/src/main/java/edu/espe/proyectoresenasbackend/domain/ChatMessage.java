package edu.espe.proyectoresenasbackend.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "es_simulado")
    private Boolean esSimulado = false;

    public ChatMessage() {
        this.fechaEnvio = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Boolean getEsSimulado() {
        return esSimulado;
    }

    public void setEsSimulado(Boolean esSimulado) {
        this.esSimulado = esSimulado;
    }
}