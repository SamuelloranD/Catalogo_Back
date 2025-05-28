package com.example.catalogo.services;

import com.example.catalogo.models.Produtos;
import com.example.catalogo.repositories.ProdutosRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

// Adicione esta importação para logs, se ainda não tiver
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProdutosService {

    private static final Logger logger = LoggerFactory.getLogger(ProdutosService.class);

    @Autowired
    private ProdutosRepository repository;

    public List<Produtos> listarTodos() {
        return repository.findAll();
    }

    public Produtos buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public Produtos salvar(Produtos produto) {
        return repository.save(produto);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void importarProdutos(List<Produtos> produtos) {
        for (Produtos produto : produtos) {
            if (produto.getCodigo() == null || produto.getCodigo().isEmpty()) {
                throw new IllegalArgumentException("Código é obrigatório para: " + produto.getNome());
            }
        }

        repository.saveAll(produtos);
    }

    public String uploadImagem(MultipartFile imagem) throws IOException {
        String diretorio = "src/main/resources/static/imagens/produtos/";
        String nomeArquivo = System.currentTimeMillis() + "_" + imagem.getOriginalFilename();
        Path caminho = Paths.get(diretorio + nomeArquivo);

        Files.createDirectories(caminho.getParent());
        Files.write(caminho, imagem.getBytes());

        return "/imagens/produtos/" + nomeArquivo;
    }

    public List<Produtos> buscarPorCategoria(String categoria) {
        logger.info("Buscando produtos pela categoria (usando @Query com LOWER()): {}", categoria);
        // Mude a chamada para o novo método findByCategoriaPersonalizada
        List<Produtos> produtosEncontrados = repository.findByCategoriaPersonalizada(categoria);
        logger.info("Encontrados {} produtos para a categoria '{}'.", produtosEncontrados.size(), categoria);
        return produtosEncontrados;
    }
}