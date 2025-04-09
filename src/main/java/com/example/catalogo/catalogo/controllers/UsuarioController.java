package com.example.catalogo.catalogo.controllers;

import com.example.catalogo.catalogo.models.Usuario;
import com.example.catalogo.catalogo.services.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @PostMapping("/enviar")
    public ResponseEntity<String> cadastrarUsuario(
            @ModelAttribute @Valid Usuario usuario,
            @RequestParam("csenha") String csenha) {

        if (!usuario.getSenha().equals(csenha)) {
            return ResponseEntity.badRequest().body("As senhas não coincidem.\nTente novamente!");
        }

        usuarioService.criarUsuario(usuario);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!\nVocê será redirecionado em 5 segundos.");
    }


    @PostMapping("/login")
    public void login(@RequestParam("usuario") String usuario,
                      @RequestParam("senha") String senha,
                      HttpServletResponse response) throws IOException {

        Usuario usuarioEncontrado = usuarioService.buscarPorUsuario(usuario);

        if (usuarioEncontrado != null && usuarioEncontrado.getSenha().equals(senha)) {
            response.sendRedirect("/index.html");
        } else {
            response.sendRedirect("/login.html?erro=true");
        }
    }

    @GetMapping("/listar")
    @ResponseStatus(HttpStatus.OK)
    public List<Usuario> listarTodosUsuarios(){
        return usuarioService.listarTodosUsuarios();
    }

}
