package br.com.gastosmensais.controller;

import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.entity.Parcela;
import br.com.gastosmensais.repository.GastoRepository;
import br.com.gastosmensais.repository.ParcelaRepository;
import br.com.gastosmensais.util.TestAuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private ParcelaRepository parcelaRepository;

    @Autowired
    private TestAuthUtil testAuthUtil;

    @BeforeEach
    void setup() {
        gastoRepository.deleteAll();
        parcelaRepository.deleteAll();

        String usuarioId = testAuthUtil.getUsuarioPadraoId();

        // Gasto 1 - Curso Java 3x
        Gasto curso = gastoRepository.save(Gasto.builder()
                .descricao("Curso Java")
                .valorTotal(new BigDecimal("1200.00"))
                .categoria("Educação")
                .tipoPagamento("Cartão")
                .parcelas(3)
                .usuarioId(usuarioId)
                .dataCompra(LocalDateTime.of(2025, 11, 5, 0, 0))
                .build());

        // Gasto 2 - Supermercado à vista
        Gasto mercado = gastoRepository.save(Gasto.builder()
                .descricao("Supermercado")
                .valorTotal(new BigDecimal("800.00"))
                .categoria("Alimentação")
                .tipoPagamento("Débito")
                .parcelas(1)
                .usuarioId(usuarioId)
                .dataCompra(LocalDateTime.of(2025, 11, 8, 0, 0))
                .build());

        parcelaRepository.saveAll(List.of(
                Parcela.builder()
                        .numero(1)
                        .valor(new BigDecimal("400.00"))
                        .dataVencimento(LocalDate.of(2025, 11, 5))
                        .gastoId(curso.getId())
                        .usuarioId(usuarioId)
                        .descricao("Curso Java")
                        .categoria("Educação")
                        .build(),
                Parcela.builder()
                        .numero(1)
                        .valor(new BigDecimal("800.00"))
                        .dataVencimento(LocalDate.of(2025, 11, 8))
                        .gastoId(mercado.getId())
                        .usuarioId(usuarioId)
                        .descricao("Supermercado")
                        .categoria("Alimentação")
                        .build()
        ));
    }

    @Test
    void deveRetornarResumoMensalBaseadoNasParcelas() throws Exception {
        String token = testAuthUtil.gerarTokenParaUsuarioPadrao();

        mockMvc.perform(get("/gastos/resumo")
                        .param("mes", "11")
                        .param("ano", "2025")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1200.00))
                .andExpect(jsonPath("$.porCategoria.Educação").value(400.00))
                .andExpect(jsonPath("$.porCategoria.Alimentação").value(800.00))
                .andExpect(jsonPath("$.quantidade").value(2));
    }
}