package com.example.catalogo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produtos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private Double preco55ml;
    private Double preco100ml;

    private Double preco;

    private String codigo;

    private String categoria;

    private String imagemUrl;

    private String descricao;

    private String volume;
}
