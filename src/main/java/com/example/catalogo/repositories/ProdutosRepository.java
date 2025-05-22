package com.example.catalogo.repositories;

import com.example.catalogo.models.Produtos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutosRepository extends JpaRepository<Produtos, Long> {
    @Query("SELECT p FROM Produtos p WHERE LOWER(p.categoria) = LOWER(:categoria)")
    List<Produtos> findByCategoriaPersonalizada(@Param("categoria") String categoria); // Renomeei para evitar conflito e deixar claro que é custom
}
