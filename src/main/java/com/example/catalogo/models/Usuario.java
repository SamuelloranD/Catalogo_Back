package com.example.catalogo.models;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario")
@Data
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String senha;
    private String email;

    private String role;

    // Não persiste o campo csenha no banco de dados, é usado apenas para validação
    /*@Transient
    private String csenha;*/

    /*public String getCsenha() {
        return csenha;
    }

    public void setCsenha(String csenha) {
        this.csenha = csenha;
    }*/
}
