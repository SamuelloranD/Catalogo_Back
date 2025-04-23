package com.example.catalogo.controllers;

import com.example.catalogo.models.Produto;
import com.example.catalogo.services.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public List<Produto> listarTodos() {
        return produtoService.listarTodos();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Produto> criarProduto(
            @RequestParam String nome,
            @RequestParam String descricao,
            @RequestParam Double preco55ml,  // Alterado
            @RequestParam Double preco100ml, // Novo
            @RequestParam String codigo,
            @RequestParam String categoria,
            @RequestParam(required = false) MultipartFile imagem) {

        try {
            Produto produto = new Produto();
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setPreco55ml(preco55ml);    // Alterado
            produto.setPreco100ml(preco100ml);  // Novo
            produto.setCodigo(codigo);
            produto.setCategoria(categoria);

            if (imagem != null && !imagem.isEmpty()) {
                produto.setImagemUrl(produtoService.uploadImagem(imagem));
            }

            return ResponseEntity.ok(produtoService.salvar(produto));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(
            @PathVariable Long id,
            @RequestBody Produto produto) {
        return ResponseEntity.ok(produtoService.atualizar(id, produto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoria}")
    public List<Produto> getPorCategoria(@PathVariable String categoria) {
        return produtoService.buscarPorCategoria(categoria);
    }

    @PostMapping("/importar")
    public ResponseEntity<?> importarProdutos(@RequestBody List<Map<String, Object>> produtosData) {
        try {
            List<Produto> produtos = produtosData.stream().map(data -> {
                Produto produto = new Produto();
                produto.setNome((String) data.get("nome"));
                produto.setDescricao((String) data.get("descricao"));
                produto.setPreco55ml(convertToDouble(data.get("volume55")));
                produto.setPreco100ml(convertToDouble(data.get("volume100")));
                produto.setCodigo((String) data.get("codigo"));
                produto.setCategoria(validarCategoria((String) data.get("categoria")));
                produto.setImagemUrl("/imagens/produtos/" + data.get("imagem"));

                // Remova esta linha se não for usar o campo destaque
                // produto.setDestaque(Boolean.TRUE.equals(data.get("destaque")));

                return produto;
            }).collect(Collectors.toList());

            produtoService.importarProdutos(produtos);
            return ResponseEntity.ok(Map.of(
                    "message", "Produtos importados com sucesso",
                    "count", produtos.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Erro na importação",
                    "details", e.getMessage()
            ));
        }
    }

    private Double convertToDouble(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        }
        return 0.0;
    }

    private String validarCategoria(String categoria) {
        List<String> categoriasValidas = List.of("masculino", "feminino", "hidratante");
        return categoriasValidas.contains(categoria) ? categoria : "outros";
    }
}