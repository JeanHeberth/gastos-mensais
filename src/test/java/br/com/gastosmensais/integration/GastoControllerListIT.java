package br.com.gastosmensais.integration;

import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.entity.Usuario;
import br.com.gastosmensais.repository.GastoRepository;
import br.com.gastosmensais.repository.UsuarioRepository;
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
class GastoControllerListIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestAuthUtil testAuthUtil;

    private String usuarioId;
    private String token;

    @BeforeEach
    void setup() {
        gastoRepository.deleteAll();
        usuarioRepository.deleteAll();

        // 🔹 1. Criar usuário real no banco
        Usuario usuario = usuarioRepository.save(
                Usuario.builder()
                        .nome("Usuário Teste")
                        .email("teste@example.com")
                        .senha("$2a$10$abcdefghijklmnopqrstuv") // senha fake encriptada
                        .build()
        );

        this.usuarioId = usuario.getId();

        // 🔹 2. Gerar token válido para este usuário
        this.token = testAuthUtil.gerarTokenParaUsuarioPadrao();

        // 🔹 3. Inserir gastos vinculados ao usuário logado
        gastoRepository.save(Gasto.builder()
                .usuarioId(usuarioId)
                .descricao("Internet")
                .valorTotal(new BigDecimal("120.00"))
                .categoria("Serviços")
                .tipoPagamento("Cartão")
                .parcelas(1)
                .dataCompra(LocalDate.of(2025, 11, 5))
                .build());

        gastoRepository.save(Gasto.builder()
                .usuarioId(usuarioId)
                .descricao("Supermercado")
                .valorTotal(new BigDecimal("500.00"))
                .categoria("Alimentação")
                .tipoPagamento("Débito")
                .parcelas(1)
                .dataCompra(LocalDate.of(2025, 10, 15))
                .build());
    }

    @Test
    void deveListarGastosPorMesEAno() throws Exception {

        mockMvc.perform(get("/gastos")
                        .param("mes", "11")
                        .param("ano", "2025")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].descricao").value("Internet"))
                .andExpect(jsonPath("$[0].valorTotal").value(120.00));
    }
}
