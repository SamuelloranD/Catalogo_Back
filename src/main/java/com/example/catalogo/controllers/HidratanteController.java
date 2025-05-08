package com.example.catalogo.controllers;

import com.example.catalogo.models.Hidratante;
import com.example.catalogo.services.HidratanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hidratantes")
public class HidratanteController {
    @Autowired
    private HidratanteService hidratanteService;

    @GetMapping
    public List<Hidratante> listarTodos() {
        return hidratanteService.listarTodos();
    }

    @GetMapping("/form/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        model.addAttribute("hidratante", hidratanteService.buscarPorId(id));
        return "hidratante-form"; // Nome do arquivo HTML (deve estar em templates/)
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hidratante> buscarPorId(@PathVariable Long id) {
        Hidratante hidratante = hidratanteService.buscarPorId(id);
        return ResponseEntity.ok(hidratante);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Hidratante> criarHidratante(
            @RequestParam String nome,
            @RequestParam String descricao,
            @RequestParam Double preco,
            @RequestParam String codigo,
            @RequestParam String volume,
            @RequestParam(required = false) MultipartFile imagem) {

        try {
            Hidratante hidratante = new Hidratante();
            hidratante.setNome(nome);
            hidratante.setDescricao(descricao);
            hidratante.setPreco(preco);
            hidratante.setCodigo(codigo);
            hidratante.setVolume(volume);

            if (imagem != null && !imagem.isEmpty()) {
                hidratante.setImagemUrl(hidratanteService.uploadImagem(imagem));
            }

            return ResponseEntity.ok(hidratanteService.salvar(hidratante));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Hidratante> atualizarHidratante(
            @PathVariable Long id,
            @RequestParam String nome,
            @RequestParam String descricao,
            @RequestParam Double preco,
            @RequestParam String codigo,
            @RequestParam String volume,
            @RequestParam(required = false) MultipartFile imagem) {

        try {
            Hidratante hidratante = hidratanteService.buscarPorId(id);
            hidratante.setNome(nome);
            hidratante.setDescricao(descricao);
            hidratante.setPreco(preco);
            hidratante.setCodigo(codigo);
            hidratante.setVolume(volume);

            if (imagem != null && !imagem.isEmpty()) {
                hidratante.setImagemUrl(hidratanteService.uploadImagem(imagem));
            }

            return ResponseEntity.ok(hidratanteService.salvar(hidratante));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarHidratante(@PathVariable Long id) {
        hidratanteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/importar")
    public ResponseEntity<?> importarHidratantes(@RequestBody List<Map<String, Object>> hidratantesData) {
        try {
            List<Hidratante> hidratantes = hidratantesData.stream().map(data -> {
                Hidratante hidratante = new Hidratante();
                hidratante.setNome((String) data.get("nome"));
                hidratante.setDescricao((String) data.get("descricao"));
                hidratante.setPreco(Double.valueOf(data.get("preco").toString()));
                hidratante.setCodigo((String) data.get("codigo"));
                hidratante.setVolume((String) data.get("volume"));
                hidratante.setImagemUrl("/imagens_hidratantes/" + data.get("imagem"));
                return hidratante;
            }).collect(Collectors.toList());

            hidratanteService.importarHidratantes(hidratantes);
            return ResponseEntity.ok(Map.of(
                    "message", "Hidratantes importados com sucesso",
                    "count", hidratantes.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Erro na importação",
                    "details", e.getMessage()
            ));
        }
    }
}