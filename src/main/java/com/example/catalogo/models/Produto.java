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
    private Double preco55ml;  // Novo campo
    private Double preco100ml; // Novo campo
    private String codigo;
    private String categoria;  // "masculino", "feminino", "hidratante"
    private String imagemUrl;
    private String descricao;  // Adicione se necessário
}