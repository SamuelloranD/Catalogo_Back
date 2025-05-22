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

    // Para perfumes (2 tamanhos), usa esses campos:
    private Double preco55ml;
    private Double preco100ml;

    // Para hidratantes (apenas um preço fixo):
    private Double preco;

    private String codigo;

    private String categoria; // Ex: "Feminino", "Masculino", ou "Hidratante"

    private String imagemUrl;

    private String descricao;

    private String volume; // Usado só nos hidratantes (ou pode deixar nulo)
}
