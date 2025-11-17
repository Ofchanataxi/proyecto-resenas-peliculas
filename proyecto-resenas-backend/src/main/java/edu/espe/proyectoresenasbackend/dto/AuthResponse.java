package edu.espe.proyectoresenasbackend.dto;


// DTO para la respuesta de autenticación
public class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
