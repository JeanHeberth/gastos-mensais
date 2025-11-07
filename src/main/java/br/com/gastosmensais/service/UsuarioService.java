package br.com.gastosmensais.service;


import br.com.gastosmensais.dto.usuario.UsuarioRequestDTO;
import br.com.gastosmensais.dto.usuario.UsuarioResponseDTO;
import br.com.gastosmensais.entity.Usuario;
import br.com.gastosmensais.enums.ForcaSenha;
import br.com.gastosmensais.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;


    public UsuarioResponseDTO salvar(UsuarioRequestDTO usuarioRequestDTO) {

        ForcaSenha forca = verificarForcaSenha(usuarioRequestDTO.senha());
        if (forca == ForcaSenha.FRACA) {
            throw new IllegalArgumentException("Senha muito fraca. Por favor, escolha uma senha mais forte.");
        }

        if (usuarioRepository.findByEmail(usuarioRequestDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado. Por favor, escolha outro email.");
        }

        Usuario usuario = Usuario.builder()
                .nome(usuarioRequestDTO.nome())
                .email(usuarioRequestDTO.email())
                .senha(usuarioRequestDTO.senha())
                .build();

        String senhaCriptografada = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
        usuario.setSenha(senhaCriptografada);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.builder()
                .nome(usuarioSalvo.getNome())
                .email(usuarioSalvo.getEmail())
                .build();
    }

    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository
                .findAll()
                .stream()
                .map(usuario -> UsuarioResponseDTO.builder()
                        .nome(usuario.getNome())
                        .email(usuario.getEmail())
                        .build())
                .toList();
    }

    public Optional<UsuarioResponseDTO> buscarPorEmail(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        return usuario.map(usuarios -> UsuarioResponseDTO.builder()
                .nome(usuarios.getNome())
                .email(usuarios.getEmail())
                .build());
    }

    public ForcaSenha verificarForcaSenha(String senha) {
        if (senha == null || senha.length() < 8) {
            return ForcaSenha.FRACA;
        }

        boolean temMaiuscula = senha.chars().anyMatch(Character::isUpperCase);
        boolean temMinuscula = senha.chars().anyMatch(Character::isLowerCase);
        boolean temNumero = senha.chars().anyMatch(Character::isDigit);
        boolean temEspecial = senha.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:'\",.<>/?".indexOf(ch) >= 0);

        int criteriosAtendidos = 0;
        if (temMaiuscula) criteriosAtendidos++;
        if (temMinuscula) criteriosAtendidos++;
        if (temNumero) criteriosAtendidos++;
        if (temEspecial) criteriosAtendidos++;

        if (criteriosAtendidos >= 3) {
            return ForcaSenha.FORTE;
        } else if (criteriosAtendidos == 2) {
            return ForcaSenha.MEDIA;
        } else {
            return ForcaSenha.FRACA;
        }
    }
}
