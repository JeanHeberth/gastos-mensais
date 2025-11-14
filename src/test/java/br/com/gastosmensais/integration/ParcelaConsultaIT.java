package br.com.gastosmensais.integration;

import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.config.JwtUtil;
import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private String gastoId;

    @BeforeEach
    void setup() {
        parcelaRepository.deleteAll();
        gastoRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Cria usuário e token JWT
        Usuario usuario = Usuario.builder()
                .nome("Jean Heberth")
                .email("jean@example.com")
                .senha(passwordEncoder.encode("Jean123#$"))
                .build();
        usuarioRepository.save(usuario);
        token = jwtUtil.gerarToken(usuario.getEmail());

        // Cria um gasto principal
        Gasto gasto = gastoRepository.save(Gasto.builder()
                .descricao("Notebook Dell")
                .valorTotal(new BigDecimal("6000.00"))
                .categoria("Tecnologia")
                .tipoPagamento("Cartão")
                .parcelas(3)
                .dataCompra(LocalDateTime.of(2025, 11, 6, 0, 0))
                .build());

        gastoId = gasto.getId();

        // Cria 3 parcelas no banco
        List<Parcela> parcelas = List.of(
                Parcela.builder()
                        .numero(1)
                        .valor(new BigDecimal("2000.00"))
                        .dataVencimento(LocalDate.of(2025, 11, 10)) // dentro de novembro
                        .gastoId(gastoId)
                        .build(),
                Parcela.builder()
                        .numero(2)
                        .valor(new BigDecimal("2000.00"))
                        .dataVencimento(LocalDate.of(2025, 12, 10)) // dezembro
                        .gastoId(gastoId)
                        .build(),
                Parcela.builder()
                        .numero(3)
                        .valor(new BigDecimal("2000.00"))
                        .dataVencimento(LocalDate.of(2026, 1, 10)) // janeiro
                        .gastoId(gastoId)
                        .build()
        );
        parcelaRepository.saveAll(parcelas);
    }

    @Test
    void deveListarParcelasPorGastoComSucesso() throws Exception {
        mockMvc.perform(get("/parcelas/gasto/" + gastoId)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].valor", is(2000.00)))
                .andExpect(jsonPath("$[0].gastoId", is(gastoId)));
    }

    @Test
    void deveListarParcelasPorMesComSucesso() throws Exception {
        YearMonth mes = YearMonth.of(2025, 11);

        mockMvc.perform(get("/parcelas")
                        .param("mes", mes.toString())
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

        mockMvc.perform(get("/parcelas")
                        .param("mes", mes.toString())
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao").value("Notebook Dell"))
                .andExpect(jsonPath("$[0].categoria").value("Tecnologia"));
    }

}
