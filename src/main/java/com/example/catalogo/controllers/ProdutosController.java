package com.example.catalogo.controllers;

import com.example.catalogo.models.Produtos;
import com.example.catalogo.services.ProdutosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/novos-produtos")
public class ProdutosController {

    @Autowired
    private ProdutosService produtosService;

    @GetMapping("/{id}")
    public ResponseEntity<Produtos> buscarPorId(@PathVariable Long id) {
        Produtos produto = produtosService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Produtos> criarProduto(
            @RequestParam String nome,
            @RequestParam String descricao,
            @RequestParam Double preco,
            @RequestParam String volume,
            @RequestParam String codigo,
            @RequestParam String categoria,
            @RequestParam(required = false) MultipartFile imagem) {

        try {
            Produtos produto = new Produtos();
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setPreco(preco);
            produto.setVolume(volume);
            produto.setCodigo(codigo);
            produto.setCategoria(categoria);

            if (imagem != null && !imagem.isEmpty()) {
                produto.setImagemUrl(produtosService.uploadImagem(imagem));
            }

            return ResponseEntity.ok(produtosService.salvar(produto));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Produtos> atualizarProduto(
            @PathVariable Long id,
            @RequestParam String nome,
            @RequestParam String descricao,
            @RequestParam Double preco,
            @RequestParam String volume,
            @RequestParam String codigo,
            @RequestParam String categoria,
            @RequestParam(required = false) MultipartFile imagem) {

        try {
            Produtos produto = produtosService.buscarPorId(id);
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setPreco(preco);
            produto.setVolume(volume);
            produto.setCodigo(codigo);
            produto.setCategoria(categoria);

            if (imagem != null && !imagem.isEmpty()) {
                produto.setImagemUrl(produtosService.uploadImagem(imagem));
            }

            return ResponseEntity.ok(produtosService.salvar(produto));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtosService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<Produtos> listarProdutos(@RequestParam(required = false) String categoria) {
        if (categoria != null && !categoria.isEmpty()) {
            return produtosService.buscarPorCategoria(categoria);
        }
        return produtosService.listarTodos();
    }

}
