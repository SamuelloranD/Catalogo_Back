package com.example.catalogo.controllers;

import com.example.catalogo.models.Usuario;
import com.example.catalogo.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        usuario.setRole("USER");

        usuarioService.cadastrarUsuario(usuario);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!\nVocê será redirecionado em 5 segundos.");
    }

    @PostMapping("/login")
    public void login(@RequestParam("usuario") String usuario,
                      @RequestParam("senha") String senha,
                      HttpServletResponse response) throws IOException {

        Usuario usuarioEncontrado = usuarioService.buscarPorUsuario(usuario);

        if (usuarioEncontrado != null && usuarioEncontrado.getSenha().equals(senha)) {
            if ("ADMIN".equals(usuarioEncontrado.getRole())) {
                response.sendRedirect("/index.html?admin=true");
            } else {
                response.sendRedirect("/index.html?admin=false");
            }
        } else {
            response.sendRedirect("/login.html?erro=true");
        }
    }

    @GetMapping("/listar")
    @ResponseStatus(HttpStatus.OK)
    public List<Usuario> listarTodosUsuarios(){
        return usuarioService.listarTodosUsuarios();
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login?logout";
    }

    @GetMapping("/api/check-admin")
    @ResponseBody
    public Map<String, Boolean> checkAdminStatus(HttpServletRequest request) {
        Map<String, Boolean> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        response.put("isAdmin", isAdmin);
        return response;
    }


}