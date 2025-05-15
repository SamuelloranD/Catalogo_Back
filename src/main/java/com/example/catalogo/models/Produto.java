package com.example.catalogo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "produto")
@Data
@Entity
public class Produto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Double preco55ml;
    private Double preco100ml;
    private String codigo;
    private String categoria;
    private String imagemUrl;
    private String descricao;
}