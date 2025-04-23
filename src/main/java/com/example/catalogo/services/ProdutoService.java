package com.example.catalogo.services;

import com.example.catalogo.models.Produto;
import com.example.catalogo.repositories.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    // Método para listar todos os produtos
    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    // Método para buscar por categoria (novo)
    public List<Produto> buscarPorCategoria(String categoria) {
        return repository.findByCategoria(categoria);
    }

    // Método para salvar novo produto
    public Produto salvar(Produto produto) {
        return repository.save(produto);
    }

    // Método para atualizar produto existente
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        return repository.findById(id)
                .map(produto -> {
                    produto.setNome(produtoAtualizado.getNome());
                    produto.setPreco55ml(produtoAtualizado.getPreco55ml());
                    produto.setPreco100ml(produtoAtualizado.getPreco100ml());
                    produto.setCodigo(produtoAtualizado.getCodigo());
                    produto.setCategoria(produtoAtualizado.getCategoria());
                    produto.setImagemUrl(produtoAtualizado.getImagemUrl());
                    produto.setDescricao(produtoAtualizado.getDescricao());
                    return repository.save(produto);
                })
                .orElseGet(() -> {
                    produtoAtualizado.setId(id);
                    return repository.save(produtoAtualizado);
                });
    }

    // Método para deletar produto
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    // Método para importação em massa (novo)
    @Transactional
    public void importarProdutos(List<Produto> produtos) {
        // Validações adicionais
        for (Produto produto : produtos) {
            if (produto.getPreco100ml() <= produto.getPreco55ml()) {
                throw new IllegalArgumentException(
                        "Preço 100ml deve ser maior que 55ml para: " + produto.getNome()
                );
            }

            if (produto.getCodigo() == null || produto.getCodigo().isEmpty()) {
                throw new IllegalArgumentException(
                        "Código é obrigatório para: " + produto.getNome()
                );
            }
        }

        repository.saveAll(produtos);
    }

    // Método para upload de imagens
    public String uploadImagem(MultipartFile imagem) throws IOException {
        String diretorio = "src/main/resources/static/imagens/produtos/";
        String nomeArquivo = System.currentTimeMillis() + "_" + imagem.getOriginalFilename();
        Path caminho = Paths.get(diretorio + nomeArquivo);

        Files.createDirectories(caminho.getParent());
        Files.write(caminho, imagem.getBytes());

        return "/imagens/produtos/" + nomeArquivo;
    }
}