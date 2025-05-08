package com.example.catalogo.services;

import com.example.catalogo.models.Hidratante;
import com.example.catalogo.repositories.HidratanteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class HidratanteService {

    @Autowired
    private HidratanteRepository repository;

    public List<Hidratante> listarTodos() {
        return repository.findAll();
    }

    public Hidratante salvar(Hidratante hidratante) {
        return repository.save(hidratante);
    }

    public Hidratante atualizar(Long id, Hidratante hidratanteAtualizado) {
        return repository.findById(id)
                .map(hidratante -> {
                    hidratante.setNome(hidratanteAtualizado.getNome());
                    hidratante.setPreco(hidratanteAtualizado.getPreco());
                    hidratante.setCodigo(hidratanteAtualizado.getCodigo());
                    hidratante.setImagemUrl(hidratanteAtualizado.getImagemUrl());
                    hidratante.setVolume(hidratanteAtualizado.getVolume());
                    hidratante.setDescricao(hidratanteAtualizado.getDescricao());
                    return repository.save(hidratante);
                })
                .orElseGet(() -> {
                    hidratanteAtualizado.setId(id);
                    return repository.save(hidratanteAtualizado);
                });
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void importarHidratantes(List<Hidratante> hidratantes) {
        for (Hidratante hidratante : hidratantes) {
            if (hidratante.getCodigo() == null || hidratante.getCodigo().isEmpty()) {
                throw new IllegalArgumentException(
                        "Código é obrigatório para: " + hidratante.getNome()
                );
            }
        }
        repository.saveAll(hidratantes);
    }

    public String uploadImagem(MultipartFile imagem) throws IOException {
        String diretorio = "src/main/resources/static/imagens_hidratantes/";
        String nomeArquivo = System.currentTimeMillis() + "_" + imagem.getOriginalFilename();
        Path caminho = Paths.get(diretorio + nomeArquivo);

        Files.createDirectories(caminho.getParent());
        Files.write(caminho, imagem.getBytes());

        return "/imagens_hidratantes/" + nomeArquivo;
    }

    public Hidratante buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hidratante não encontrado"));
    }
}