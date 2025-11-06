package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.gasto.response.GastoResponseDTO;
import br.com.gastosmensais.repository.GastoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GastoServiceTest {

    @Mock
    private GastoRepository gastoRepository;

    @Mock
    private ParcelaService parcelaService;

    @InjectMocks
    private GastoService gastoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarGastoComParcelas() {
        GastoRequestDTO dto = new GastoRequestDTO(
                "Notebook",
                new BigDecimal("6000.00"),
                "Tecnologia",
                "Cartão",
                3,
                LocalDateTime.now()
        );

        GastoResponseDTO response = gastoService.criarGastos(dto);

        assertThat(response).isNotNull();
        assertThat(response.descricao()).isEqualTo("Notebook");
    }
}

