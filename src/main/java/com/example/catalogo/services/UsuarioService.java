package com.example.catalogo.services;

import com.example.catalogo.models.Usuario;
import com.example.catalogo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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