package br.com.gastosmensais.controller;

import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // ✅ agora está no local correto
class GastoControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarGastoComSucesso() throws Exception {
        GastoRequestDTO gastoRequestDTO = new GastoRequestDTO(
                "Celular",
                new BigDecimal("3000.00"),
                "Eletrônico",
                "Cartão",
                3,
                LocalDateTime.of(2025, 11, 6, 0, 0)
        );

        mockMvc.perform(post("/api/gastos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gastoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Celular"));
    }
}






