package br.com.gastosmensais.integration;

import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.config.JwtUtil;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.entity.Parcela;
import br.com.gastosmensais.entity.Usuario;
import br.com.gastosmensais.repository.GastoRepository;
import br.com.gastosmensais.repository.ParcelaRepository;
import br.com.gastosmensais.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ParcelaConsultaIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private GastoRepository gastoRepository;
    @Autowired
    private ParcelaRepository parcelaRepository;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String token;
    private String usuarioId;
    private String gastoId;

    @BeforeEach
    void setup() {
        parcelaRepository.deleteAll();
        gastoRepository.deleteAll();
        usuarioRepository.deleteAll();

        // 🔐 1. Cria usuário e token
        Usuario usuario = usuarioRepository.save(
                Usuario.builder()
                        .nome("Jean Heberth")
                        .email("jean@example.com")
                        .senha(passwordEncoder.encode("Jean123#$"))
                        .build()
        );

        usuarioId = usuario.getId();
        token = jwtUtil.gerarToken(usuario.getEmail());

        // 🧾 2. Criar Gasto vinculado ao usuário
        Gasto gasto = gastoRepository.save(
                Gasto.builder()
                        .descricao("Notebook Dell")
                        .valorTotal(new BigDecimal("6000.00"))
                        .categoria("Tecnologia")
                        .tipoPagamento("Cartão")
                        .parcelas(3)
                        .dataCompra(LocalDateTime.of(2025, 11, 6, 0, 0))
                        .usuarioId(usuarioId) // OBRIGATÓRIO
                        .build()
        );

        gastoId = gasto.getId();

        // 💳 3. Criar Parcelas completas e com usuarioId
        parcelaRepository.saveAll(List.of(
                Parcela.builder()
                        .numero(1)
                        .valor(new BigDecimal("2000.00"))
                        .dataVencimento(LocalDate.of(2025, 11, 10))
                        .gastoId(gastoId)
                        .descricao("Notebook Dell")
                        .categoria("Tecnologia")
                        .usuarioId(usuarioId)
                        .build(),

                Parcela.builder()
                        .numero(2)
                        .valor(new BigDecimal("2000.00"))
                        .dataVencimento(LocalDate.of(2025, 12, 10))
                        .gastoId(gastoId)
                        .descricao("Notebook Dell")
                        .categoria("Tecnologia")
                        .usuarioId(usuarioId)
                        .build(),

                Parcela.builder()
                        .numero(3)
                        .valor(new BigDecimal("2000.00"))
                        .dataVencimento(LocalDate.of(2026, 1, 10))
                        .gastoId(gastoId)
                        .descricao("Notebook Dell")
                        .categoria("Tecnologia")
                        .usuarioId(usuarioId)
                        .build()
        ));
    }

    @Test
    void deveListarParcelasPorGastoComSucesso() throws Exception {
        mockMvc.perform(get("/parcelas/gasto/" + gastoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].valor", is(2000.00)))
                .andExpect(jsonPath("$[0].gastoId", is(gastoId)));
    }

    @Test
    void deveListarParcelasPorMesComSucesso() throws Exception {
        YearMonth mes = YearMonth.of(2025, 11);

        mockMvc.perform(get("/parcelas/mes/" + mes)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dataVencimento").value("2025-11-10"))
                .andExpect(jsonPath("$[0].valor").value(2000.00))
                .andExpect(jsonPath("$[0].descricao").value("Notebook Dell"))
                .andExpect(jsonPath("$[0].categoria").value("Tecnologia"));
    }


    @Test
    void deveNegarAcessoSemToken() throws Exception {
        mockMvc.perform(get("/parcelas/gasto/" + gastoId))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveListarParcelasComDescricaoECategoria() throws Exception {
        YearMonth mes = YearMonth.of(2025, 11);

        mockMvc.perform(get("/parcelas/mes/" + mes)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao").value("Notebook Dell"))
                .andExpect(jsonPath("$[0].categoria").value("Tecnologia"));
    }
}
