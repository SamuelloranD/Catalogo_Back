package com.example.catalogo.models;

import com.fasterxml.jackson.annotation.JsonProperty; // <-- Novo import!
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produtos produto;

    private int quantidade;

    private BigDecimal precoUnitario;

    @ManyToOne
    private Venda venda;

    @JsonProperty("produtoId")
    public void setProdutoId(Long id) {
        this.produto = new Produtos();
        this.produto.setId(id);
    }
}