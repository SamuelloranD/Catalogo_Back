package com.example.catalogo.catalogo.services;

import com.example.catalogo.catalogo.models.Usuario;
import com.example.catalogo.catalogo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public void cadastrarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario); // Salvar no banco de dados
    }

    public Usuario buscarPorUsuario(String usuario) {
        return usuarioRepository.findByUsername(usuario);
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }
}