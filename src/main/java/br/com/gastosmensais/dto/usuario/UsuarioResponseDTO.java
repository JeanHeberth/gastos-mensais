package br.com.gastosmensais.dto.usuario;


import lombok.Builder;

@Builder
public record UsuarioResponseDTO(
        String id,
        String nome,
        String email) {
}
