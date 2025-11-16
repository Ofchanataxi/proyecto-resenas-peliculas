package edu.espe.proyectoresenasbackend.domain;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority; // <-- Importar
import org.springframework.security.core.authority.SimpleGrantedAuthority; // <-- Importar
import org.springframework.security.core.userdetails.UserDetails; // <-- Importar
import java.util.Collection; // <-- Importar
import java.util.List; // <-- Importar

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 120)
    private  String email;

    @Column(nullable = false, length = 255)
    private String contrasena;

    private Boolean activo = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por ahora, todos son usuarios. Puedes hacerlo más complejo después.
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return contrasena; // Devuelve el campo de la contraseña hasheada
    }

    @Override
    public String getUsername() {
        return email; // Usamos el email como username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // La cuenta nunca expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // La cuenta nunca se bloquea
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las credenciales nunca expiran
    }

    @Override
    public boolean isEnabled() {
        return activo; // El usuario está habilitado si está activo
    }
}
