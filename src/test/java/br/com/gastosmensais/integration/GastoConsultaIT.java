package br.com.gastosmensais.integration;


import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.config.JwtUtil;
import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.entity.Usuario;
import br.com.gastosmensais.repository.UsuarioRepository;
import br.com.gastosmensais.util.TestAuthUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GastoConsultaIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TestAuthUtil testAuthUtil;



    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String token;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();

        // Cria e persiste usuário de teste
        Usuario usuario = Usuario.builder()
                .nome("Jean Heberth")
                .email("jean@example.com")
                .senha(passwordEncoder.encode("Jean123#$"))
                .build();
        usuarioRepository.save(usuario);

        // Gera token JWT válido
        token = jwtUtil.gerarToken(usuario.getEmail());
    }

    @Test
    void deveListarGastosDoUsuarioComSucesso() throws Exception {
        // Cria gasto via endpoint
        GastoRequestDTO gasto = new GastoRequestDTO(
                "Notebook Dell",
                new BigDecimal("6000.00"),
                "Tecnologia",
                "Cartão",
                3,
                LocalDateTime.of(2025, 11, 6, 0, 0)
        );

        String token = testAuthUtil.gerarTokenParaUsuarioPadrao();

        mockMvc.perform(post("/gastos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gasto))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        // Lista gastos
        mockMvc.perform(get("/gastos")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao", is("Notebook Dell")))
                .andExpect(jsonPath("$[0].valorTotal", is(6000.00)))
                .andExpect(jsonPath("$[0].categoria", is("Tecnologia")))
                .andExpect(jsonPath("$[0].tipoPagamento", is("Cartão")))
                .andExpect(jsonPath("$[0].parcelas", is(3)));
    }

    @Test
    void deveNegarAcessoSemTokenJWT() throws Exception {
        mockMvc.perform(get("/gastos"))
                .andExpect(status().isForbidden());
    }
}
