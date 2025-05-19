package com.example.catalogo.controllers;

import com.example.catalogo.dtos.VendaDTO;
import com.example.catalogo.models.Usuario;
import com.example.catalogo.models.Venda;
import com.example.catalogo.services.JwtService;
import com.example.catalogo.services.UsuarioService;
import com.example.catalogo.services.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @Autowired
    JwtService jwtService;

    @Autowired
    UsuarioService usuarioService;

    @PostMapping("/vendas")
    public ResponseEntity<Venda> registrarVenda(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Venda venda) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        String username = jwtService.validarToken(token);

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioService.buscarPorUsuario(username);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        venda.setUsuario(usuario);
        Venda salva = vendaService.registrarVenda(venda);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }


    @PostMapping("/{id}/confirmar")
    public ResponseEntity<Void> confirmarVenda(@PathVariable Long id) {
        try {
            vendaService.confirmarVenda(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarVenda(@PathVariable Long id) {
        try {
            vendaService.cancelarVenda(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/todas")
    public ResponseEntity<List<VendaDTO>> listarTodasVendas() {
        List<VendaDTO> todas = vendaService.listarVendasPendentesDTO();
        todas.addAll(vendaService.listarVendasConfirmadasDTO());
        return ResponseEntity.ok(todas);
    }

    @DeleteMapping("/{id}/apagar")
    public ResponseEntity<Void> apagarVenda(@PathVariable Long id) {
        try {
            vendaService.apagarVenda(id);
            return ResponseEntity.noContent().build(); // 204
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
