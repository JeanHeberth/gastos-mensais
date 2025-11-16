package br.com.gastosmensais.util;

import br.com.gastosmensais.config.JwtUtil;
import br.com.gastosmensais.entity.Usuario;
import br.com.gastosmensais.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestAuthUtil {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public TestAuthUtil(UsuarioRepository usuarioRepository, JwtUtil jwtUtil, BCryptPasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    private static final String EMAIL_PADRAO = "teste@example.com";
    private static final String SENHA_PADRAO = "Senha123$";

    /**
     * Garante que o usuário padrão exista e retorna seu ID
     */
    public String getUsuarioPadraoId() {
        Usuario usuario = usuarioRepository.findByEmail(EMAIL_PADRAO)
                .orElseGet(() -> usuarioRepository.save(
                        Usuario.builder()
                                .nome("Usuário Teste")
                                .email(EMAIL_PADRAO)
                                .senha(encoder.encode(SENHA_PADRAO))
                                .build()
                ));

        return usuario.getId();
    }

    /**
     * Gera um token JWT válido para o usuário padrão.
     */
    public String gerarTokenParaUsuarioPadrao() {
        String usuarioId = getUsuarioPadraoId();
        return jwtUtil.gerarToken(EMAIL_PADRAO); // token baseado no email
    }
}
