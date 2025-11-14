package br.com.gastosmensais.auth;

import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.dto.login.LoginRequestDTO;
import br.com.gastosmensais.entity.Usuario;
import br.com.gastosmensais.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void deveRegistrarUsuarioComSucesso() throws Exception {
        Usuario user = Usuario.builder()
                .nome("Jean Heberth")
                .email("jean@example.com")
                .senha("Jean123#$")
                .build();


        mockMvc.perform(post("/usuarios/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jean@example.com"))
                .andExpect(jsonPath("$.nome").value("Jean Heberth"))
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    void deveRealizarLoginComSucessoERetornarTokenJWT() throws Exception {
        // Inserindo usuário criptografado manualmente no banco
        userRepository.save(Usuario.builder()
                .nome("Jean Heberth")
                .email("jean@example.com")
                .senha(passwordEncoder.encode("Jean123#$"))
                .build());

        LoginRequestDTO request = new LoginRequestDTO("jean@example.com", "Jean123#$");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyOrNullString())));
    }

    @Test
    void deveRetornarErroAoLogarComSenhaInvalida() throws Exception {
        userRepository.save(Usuario.builder()
                .nome("Jean Heberth")
                .email("jean@example.com")
                .senha(passwordEncoder.encode("123456"))
                .build());

        LoginRequestDTO request = new LoginRequestDTO("jean@example.com", "senhaErrada");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.erro", containsString("Credenciais inválidas")));
    }

    @Test
    void deveRetornarErroAoLogarComUsuarioInexistente() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("naoexiste@example.com", "123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.erro", containsString("Usuário não encontrado")));
    }
}
