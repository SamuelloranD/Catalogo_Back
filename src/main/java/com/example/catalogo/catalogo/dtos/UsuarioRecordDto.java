package com.example.catalogo.catalogo.dtos;

import jakarta.validation.constraints.NotBlank;

public record UsuarioRecordDto(
        @NotBlank String username,
        @NotBlank String senha,
        @NotBlank String email,
        @NotBlank String csenha
) {}
