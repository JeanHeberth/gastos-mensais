package br.com.gastosmensais.service;


import br.com.gastosmensais.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void deveTratarErroDeValidacaoComMensagemClara() {
        // 🔹 Simula um objeto inválido com erros de binding
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "gastoRequestDTO");
        bindingResult.addError(new FieldError("gastoRequestDTO", "descricao", "A descrição é obrigatória."));
        bindingResult.addError(new FieldError("gastoRequestDTO", "valorTotal", "O valor total deve ser maior que zero."));

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        // 🔹 Executa o handler
        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(exception);

        // 🔹 Verifica status e estrutura
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Erro de validação");
        assertThat(response.getBody().get("status")).isEqualTo(400);

        // 🔹 Verifica mensagens específicas
        Map<String, String> messages = (Map<String, String>) response.getBody().get("messages");
        assertThat(messages)
                .containsEntry("descricao", "A descrição é obrigatória.")
                .containsEntry("valorTotal", "O valor total deve ser maior que zero.");
    }

    @Test
    void deveTratarRuntimeExceptionComMensagemGenerica() {
        RuntimeException ex = new RuntimeException("Erro interno inesperado");

        ResponseEntity<Map<String, String>> response = handler.handleRuntime(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("erro")).isEqualTo("Erro interno inesperado");
    }
}

