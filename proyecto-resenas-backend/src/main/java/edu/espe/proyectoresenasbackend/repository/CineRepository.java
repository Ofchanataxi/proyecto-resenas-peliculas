package edu.espe.proyectoresenasbackend.repository;

import edu.espe.proyectoresenasbackend.domain.Cine;
import org.springframework.data.jpa.repository.JpaRepository;


//Repositorio para la entidad Cine
public interface CineRepository extends JpaRepository<Cine, Long> {
}
