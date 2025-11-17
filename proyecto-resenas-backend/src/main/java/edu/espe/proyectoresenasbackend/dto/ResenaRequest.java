package edu.espe.proyectoresenasbackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// DTO para la creación o actualización de una reseña
public class ResenaRequest {
    // Campos necesarios para crear o actualizar una reseña

    private String comentario;

    @NotNull
    @Min(1) @Max(5)
    private Integer calificacion;

    @NotNull
    private Long usuarioId;

    @NotNull
    private Long peliculaId;

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getPeliculaId() {
        return peliculaId;
    }

    public void setPeliculaId(Long peliculaId) {
        this.peliculaId = peliculaId;
    }
}
