package br.com.gastosmensais.service;

import br.com.gastosmensais.config.UsuarioLogado;
import br.com.gastosmensais.entity.Usuario;
import br.com.gastosmensais.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Carregando usuário pelo e-mail: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado com o e-mail: {}", email);
                    return new UsernameNotFoundException("Usuário não encontrado");
                });

        // 👉 Agora retornamos um principal que conhece o ID do usuário
        return new UsuarioLogado(usuario);
    }
}
