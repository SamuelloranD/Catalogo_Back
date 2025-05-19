package com.example.catalogo.dtos;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VendaDTO {
    private Long id;
    private Long usuarioId;
    private String usuario;
    private LocalDateTime dataHora;
    private Boolean confirmada;
    private List<ItemVendaDTO> itens;

    public VendaDTO(Long id, Long usuarioId, String usuario, LocalDateTime dataHora, Boolean confirmada, List<ItemVendaDTO> itens) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.usuario = usuario;
        this.dataHora = dataHora;
        this.confirmada = confirmada;
        this.itens = itens;
    }
}
