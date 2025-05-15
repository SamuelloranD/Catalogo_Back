package com.example.catalogo.controllers;

import com.example.catalogo.models.Usuario;
import com.example.catalogo.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    com.example.catalogo.services.JwtService jwtService;

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
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> payload) {
        String usuario = payload.get("usuario");
        String senha = payload.get("senha");

        Usuario usuarioEncontrado = usuarioService.buscarPorUsuario(usuario);

        if (usuarioEncontrado != null && usuarioEncontrado.getSenha().equals(senha)) {
            System.out.println("Usuario encontrado" + usuarioEncontrado);
            String token = jwtService.gerarToken(usuarioEncontrado.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("admin", "ADMIN".equals(usuarioEncontrado.getRole()));
            response.put("token", token); // Envia o token para o frontend

            return ResponseEntity.ok(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Usuário ou senha incorretos.");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    @GetMapping("/listar")
    @ResponseStatus(HttpStatus.OK)
    public List<Usuario> listarTodosUsuarios(){
        return usuarioService.listarTodosUsuarios();
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout realizado com sucesso");
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