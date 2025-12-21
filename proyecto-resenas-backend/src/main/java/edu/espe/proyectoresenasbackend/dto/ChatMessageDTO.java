package edu.espe.proyectoresenasbackend.dto;

import java.time.LocalDateTime;

public class ChatMessageDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private Boolean esSimulado;

    public ChatMessageDTO() {
    }

    public ChatMessageDTO(Long id, Long usuarioId, String usuarioNombre, String contenido, LocalDateTime fechaEnvio, Boolean esSimulado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
        this.esSimulado = esSimulado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
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