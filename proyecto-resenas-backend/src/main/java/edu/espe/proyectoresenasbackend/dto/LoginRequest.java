package edu.espe.proyectoresenasbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO para la solicitud de inicio de sesión

public class LoginRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String contrasena;

    // Getters y Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
