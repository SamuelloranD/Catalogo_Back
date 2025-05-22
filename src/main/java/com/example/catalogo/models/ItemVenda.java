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
    private Produtos produto; // O nome da variável é 'produto' (singular)

    private int quantidade;

    private BigDecimal precoUnitario;

    @ManyToOne
    private Venda venda;

    // NOVO MÉTODO: Jackson vai chamar este setter quando encontrar "produtoId" no JSON
    // Ele cria um objeto Produtos temporário apenas com o ID
    @JsonProperty("produtoId") // <-- Diz ao Jackson para mapear a chave "produtoId" do JSON para este setter
    public void setProdutoId(Long id) {
        this.produto = new Produtos(); // Cria uma nova instância de Produtos
        this.produto.setId(id);        // Define apenas o ID recebido do JSON
    }

    // Você também pode querer um getter para produtoId para consistência, se precisar expor em DTOs.
    // @JsonProperty("produtoId")
    // public Long getProdutoId() {
    //    return (this.produto != null) ? this.produto.getId() : null;
    // }
}