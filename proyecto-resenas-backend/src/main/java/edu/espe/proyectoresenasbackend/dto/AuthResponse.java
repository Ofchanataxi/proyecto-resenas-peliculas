package edu.espe.proyectoresenasbackend.dto;


// DTO para la respuesta de autenticación
public class AuthResponse {
    private String token;
    private Long id;

    public AuthResponse(String token, Long id) {
        this.token = token;
        this.id = id;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
