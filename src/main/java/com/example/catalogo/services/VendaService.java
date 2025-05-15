package com.example.catalogo.services;

import com.example.catalogo.dtos.ItemVendaDTO;
import com.example.catalogo.dtos.VendaDTO;
import com.example.catalogo.models.ItemVenda;
import com.example.catalogo.models.Produto;
import com.example.catalogo.models.Venda;
import com.example.catalogo.repositories.ProdutoRepository;
import com.example.catalogo.repositories.UsuarioRepository;
import com.example.catalogo.repositories.VendaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public Venda registrarVenda(Venda venda) {
        venda.setDataHora(LocalDateTime.now());
        venda.setConfirmada(false);

        // Corrigir os itens
        List<ItemVenda> itensCorrigidos = new ArrayList<>();
        for (ItemVenda item : venda.getItens()) {
            // Buscar produto completo pelo id
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + item.getProduto().getId()));

            item.setProduto(produto);
            item.setVenda(venda);
            itensCorrigidos.add(item);
        }
        venda.setItens(itensCorrigidos);

        return vendaRepository.save(venda);
    }


    @Transactional
    public void confirmarVenda(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        venda.setConfirmada(true); // Corrigido para usar o boolean
        vendaRepository.save(venda);
    }

    public VendaDTO toDTO(Venda venda) {
        List<ItemVendaDTO> itensDto = venda.getItens().stream().map(item -> new ItemVendaDTO(
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario()
        )).collect(Collectors.toList());

        return new VendaDTO(
                venda.getId(),
                venda.getUsuario().getId(),       // Passa o ID do usuário
                venda.getUsuario().getUsername(), // Passa o username
                venda.getDataHora(),
                venda.getConfirmada(),
                itensDto
        );
    }


    public List<Venda> listarVendasConfirmadas() {
        return vendaRepository.findByConfirmadaTrue();
    }

    public List<Venda> buscarVendasPendentes() {
        return vendaRepository.findByConfirmadaFalse();
    }

    public List<VendaDTO> listarVendasConfirmadasDTO() {
        return listarVendasConfirmadas().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<VendaDTO> listarVendasPendentesDTO() {
        return buscarVendasPendentes().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelarVenda(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        venda.setConfirmada(false);
        vendaRepository.save(venda);
    }
}

