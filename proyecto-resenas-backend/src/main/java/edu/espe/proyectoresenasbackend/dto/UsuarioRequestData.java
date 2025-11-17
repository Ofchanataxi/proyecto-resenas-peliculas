package edu.espe.proyectoresenasbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuarioRequestData {
    //Usuarios
    @NotBlank @Size(min = 3, max=120)
    private String nombreCompleto;

    @NotBlank @Email
    @Size(max = 120)
    private String email;

    @NotBlank
    @Size(min = 8, max = 100) // Validación simple para la contraseña
    private String contrasena;

    private boolean activo; // Para el 'update'

    // Getters y Setters...
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public boolean getActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
