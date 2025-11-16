package br.com.gastosmensais.integration;

import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.util.TestAuthUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GastoControllerValidationIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestAuthUtil testAuthUtil;

    @Test
    void deveRetornarErroQuandoCamposForemInvalidos() throws Exception {
        // 🔹 Gasto com campos inválidos (simulando falha de validação)
        GastoRequestDTO requestInvalido = new GastoRequestDTO(
                null, // descrição vazia → deve falhar com @NotNull/@Size
                BigDecimal.ZERO, // valor inválido → deve falhar com @Positive
                null, // categoria vazia → deve falhar com @NotBlank
                null, // tipo de pagamento vazio → deve falhar com @NotBlank
                0, // parcelas inválidas → deve falhar com @Positive
                null // data nula → deve falhar com @NotNull
        );

        String token = testAuthUtil.gerarTokenParaUsuarioPadrao();
        mockMvc.perform(post("/gastos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido))
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro de validação"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages.descricao").value("A descrição é obrigatória."))
                .andExpect(jsonPath("$.messages.valorTotal").value("O valor total deve ser maior que zero."))
                .andExpect(jsonPath("$.messages.categoria").value("A categoria é obrigatória."))
                .andExpect(jsonPath("$.messages.tipoPagamento").value("O tipo de pagamento é obrigatório."))
                .andExpect(jsonPath("$.messages.parcelas").value("O número de parcelas deve ser maior que zero."))
                .andExpect(jsonPath("$.messages.dataCompra").value("A data de compra é obrigatória."));
    }
}
