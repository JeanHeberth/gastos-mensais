package br.com.gastosmensais.integration;

import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.util.TestAuthUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.repository.GastoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class GastoControllerListIT extends AbstractIntegrationTest {

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
                .descricao("Internet")
                .valorTotal(new BigDecimal("120.00"))
                .categoria("Serviços")
                .tipoPagamento("Cartão")
                .parcelas(1)
                .dataCompra(LocalDateTime.of(2025, 11, 5, 0, 0))
                .build());

        gastoRepository.save(Gasto.builder()
                .descricao("Supermercado")
                .valorTotal(new BigDecimal("500.00"))
                .categoria("Alimentação")
                .tipoPagamento("Débito")
                .parcelas(1)
                .dataCompra(LocalDateTime.of(2025, 10, 15, 0, 0))
                .build());
    }

    @Test
    void deveListarGastosPorMesEAno() throws Exception {
        String token = testAuthUtil.gerarTokenParaUsuarioPadrao();

        mockMvc.perform(get("/gastos")
                        .param("mes", "11")
                        .param("ano", "2025")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].descricao").value("Internet"))
                .andExpect(jsonPath("$[0].valorTotal").value(120.00));
    }
}


