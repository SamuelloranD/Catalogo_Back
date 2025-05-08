package com.example.catalogo.repositories;

import com.example.catalogo.models.Hidratante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HidratanteRepository extends JpaRepository<Hidratante, Long> {
    // Métodos específicos podem ser adicionados aqui se necessário
}