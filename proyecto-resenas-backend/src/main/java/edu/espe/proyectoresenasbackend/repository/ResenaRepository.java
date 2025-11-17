package edu.espe.proyectoresenasbackend.repository;

import edu.espe.proyectoresenasbackend.domain.Resena;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Repositorio para la entidad Resena
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByPeliculaId(Long peliculaId);
}
