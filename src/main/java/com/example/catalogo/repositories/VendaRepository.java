package com.example.catalogo.repositories;

import com.example.catalogo.models.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByConfirmadaTrue();
    List<Venda> findByConfirmadaFalse();
    List<Venda> findByConfirmadaTrueAndDataHoraBetween(java.time.LocalDateTime inicio, java.time.LocalDateTime fim);
}

