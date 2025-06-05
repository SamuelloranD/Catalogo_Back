package com.example.catalogo.controllers;

import com.example.catalogo.models.Produtos;
import com.example.catalogo.services.ProdutosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map; // Não usado, pode remover

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
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) Double preco,
            @RequestParam(required = false) String volume,
            @RequestParam(required = false) Double preco55ml,
            @RequestParam(required = false) Double preco100ml,
            @RequestParam String codigo,
            @RequestParam String categoria,
            @RequestParam(required = false) MultipartFile imagem) {

        try {
            Produtos produto = new Produtos();
            produto.setNome(nome);
            produto.setCodigo(codigo);
            produto.setCategoria(categoria);

            produto.setDescricao(descricao);
            produto.setVolume(volume);
            produto.setPreco(preco);
            produto.setPreco55ml(preco55ml);
            produto.setPreco100ml(preco100ml);

            if (imagem != null && !imagem.isEmpty()) {
                produto.setImagemUrl(produtosService.uploadImagem(imagem));
            }

            return ResponseEntity.ok(produtosService.salvar(produto));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Método para atualizar um produto (ajustado para lidar com perfumes e outros produtos)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Produtos> atualizarProduto(
            @PathVariable Long id,
            @RequestParam String nome,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) Double preco,
            @RequestParam(required = false) String volume,
            @RequestParam(required = false) Double preco55ml,
            @RequestParam(required = false) Double preco100ml,
            @RequestParam String codigo,
            @RequestParam String categoria,
            @RequestParam(required = false) MultipartFile imagem) {

        try {
            Produtos produto = produtosService.buscarPorId(id);
            if (produto == null) {
                return ResponseEntity.notFound().build();
            }

            produto.setNome(nome);
            produto.setCodigo(codigo);
            produto.setCategoria(categoria);

            produto.setDescricao(descricao);
            produto.setVolume(volume);
            produto.setPreco(preco);
            produto.setPreco55ml(preco55ml);
            produto.setPreco100ml(preco100ml);

            if (imagem != null && !imagem.isEmpty()) {
                produto.setImagemUrl(produtosService.uploadImagem(imagem));
            }

            return ResponseEntity.ok(produtosService.salvar(produto));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
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