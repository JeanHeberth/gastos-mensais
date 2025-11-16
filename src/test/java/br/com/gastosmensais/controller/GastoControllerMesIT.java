package br.com.gastosmensais.controller;


import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.config.JwtUtil;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.entity.Usuario;
import br.com.gastosmensais.repository.GastoRepository;
import br.com.gastosmensais.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GastoControllerMesIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String token;
    private String usuarioId;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();
        gastoRepository.deleteAll();

        // Usuário para autenticação
        Usuario user = Usuario.builder()
                .nome("Jean Heberth")
                .email("jean@example.com")
                .senha(passwordEncoder.encode("Jean123#"))
                .build();

        usuarioRepository.save(user);
        usuarioId = user.getId();

        token = jwtUtil.gerarToken(user.getEmail());

        // Gasto do mês 11
        gastoRepository.save(Gasto.builder()
                .usuarioId(usuarioId)
                .descricao("Curso Java")
                .categoria("Educação")
                .valorTotal(new BigDecimal("600.00"))
                .tipoPagamento("Cartão")
                .parcelas(1)
                .dataCompra(LocalDate.of(2025, 11, 5))
                .build());

        // Outro gasto do mês anterior (não pode aparecer)
        gastoRepository.save(Gasto.builder()
                .usuarioId(usuarioId)
                .descricao("Mercado")
                .categoria("Alimentação")
                .valorTotal(new BigDecimal("300.00"))
                .tipoPagamento("Débito")
                .parcelas(1)
                .dataCompra(LocalDate.of(2025, 10, 20))
                .build());
    }

    @Test
    void deveListarGastosDoMesComSucesso() throws Exception {

        mockMvc.perform(get("/gastos/mes/2025-11")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descricao", is("Curso Java")))
                .andExpect(jsonPath("$[0].categoria", is("Educação")))
                .andExpect(jsonPath("$[0].valorTotal").value(600.00));
    }

    @Test
    void deveRetornarNoContentQuandoNaoExistirGastosNoMes() throws Exception {
        mockMvc.perform(get("/gastos/mes/2025-01")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveNegarAcessoSemToken() throws Exception {
        mockMvc.perform(get("/gastos/mes/2025-11"))
                .andExpect(status().isForbidden());
    }
}
