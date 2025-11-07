package br.com.gastosmensais.controller;


import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.repository.GastoRepository;
import br.com.gastosmensais.util.TestAuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private TestAuthUtil testAuthUtil;

    @BeforeEach
    void setup() {
        gastoRepository.deleteAll();

        gastoRepository.save(Gasto.builder()
                .descricao("Curso Java")
                .valorTotal(new BigDecimal("1200.00"))
                .categoria("Educação")
                .tipoPagamento("Cartão")
                .parcelas(3)
                .dataCompra(LocalDateTime.of(2025, 11, 5, 0, 0))
                .build());

        gastoRepository.save(Gasto.builder()
                .descricao("Supermercado")
                .valorTotal(new BigDecimal("800.00"))
                .categoria("Alimentação")
                .tipoPagamento("Débito")
                .parcelas(1)
                .dataCompra(LocalDateTime.of(2025, 11, 8, 0, 0))
                .build());
    }

    @Test
    void deveRetornarResumoMensal() throws Exception {

        String token = testAuthUtil.gerarTokenParaUsuarioPadrao();

        mockMvc.perform(get("/gastos/resumo")
                        .param("mes", "11")
                        .param("ano", "2025")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalGastos").value(2000.00))
                .andExpect(jsonPath("$.porCategoria.Educação").value(1200.00))
                .andExpect(jsonPath("$.porCategoria.Alimentação").value(800.00))
                .andExpect(jsonPath("$.quantidadeGastos").value(2));
    }
}