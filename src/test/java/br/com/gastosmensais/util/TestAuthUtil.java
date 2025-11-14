package br.com.gastosmensais.util;


import br.com.gastosmensais.config.JwtUtil;
import br.com.gastosmensais.entity.Usuario;
import br.com.gastosmensais.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestAuthUtil {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder encoder;


    public String gerarTokenParaUsuarioPadrao() {
        Usuario user = usuarioRepository.findByEmail("teste@example.com")
                .orElseGet(() -> usuarioRepository.save(
                        Usuario.builder()
                                .nome("Usuário Teste")
                                .email("teste@example.com")
                                .senha(encoder.encode("Senha123$"))
                                .build()
                ));
        return jwtUtil.gerarToken(user.getEmail());
    }
}

