package br.com.roomflow.usuarios_ms.dto;

import br.com.roomflow.usuarios_ms.model.Usuario;

public record UsuarioDto(Long id, String nome, String email) {
    public UsuarioDto(Usuario usuario) {this(usuario.getId(), usuario.getNome(), usuario.getEmail());}
}
