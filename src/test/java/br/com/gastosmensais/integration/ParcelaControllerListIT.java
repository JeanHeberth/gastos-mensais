package br.com.gastosmensais.integration;

import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.entity.Parcela;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ParcelaControllerListIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParcelaRepository parcelaRepository;

    @Autowired
    private TestAuthUtil testAuthUtil;

    @BeforeEach
    void setup() {
        parcelaRepository.deleteAll();

        parcelaRepository.save(Parcela.builder()
                .numero(1)
                .valor(new BigDecimal("200.00"))
                .dataVencimento(LocalDate.of(2025, 11, 10))
                .gastoId("gasto-xyz")
                .build());

        parcelaRepository.save(Parcela.builder()
                .numero(2)
                .valor(new BigDecimal("200.00"))
                .dataVencimento(LocalDate.of(2025, 12, 10))
                .gastoId("gasto-xyz")
                .build());
    }

    @Test
    void deveListarParcelasPorGasto() throws Exception {

        String token = testAuthUtil.gerarTokenParaUsuarioPadrao();

        mockMvc.perform(get("/parcelas/gasto/{id}", "gasto-xyz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].numero").value(1))
                .andExpect(jsonPath("$[0].valor").value(200.00))
                .andExpect(jsonPath("$[1].numero").value(2))
                .andExpect(jsonPath("$[1].gastoId").value("gasto-xyz"));
    }
}

