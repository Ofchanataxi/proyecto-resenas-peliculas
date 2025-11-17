package edu.espe.proyectoresenasbackend.repository;

import edu.espe.proyectoresenasbackend.domain.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
}
