package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.gasto.response.GastoResponseDTO;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.repository.GastoRepository;
import br.com.gastosmensais.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
        // Arrange
        GastoRequestDTO dto = TestDataFactory.criarGastoRequestPadrao();
        Gasto gastoMock = TestDataFactory.criarGastoEntityPadrao();

        when(gastoRepository.save(any(Gasto.class))).thenReturn(gastoMock);
        when(parcelaService.gerarEGuardarParcelas(any(), anyString())).thenReturn(List.of());

        // Act
        GastoResponseDTO response = gastoService.criarGastos(dto);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("gasto-123");
        assertThat(response.descricao()).isEqualTo("Notebook");
        assertThat(response.valorTotal()).isEqualByComparingTo("6000.00");
    }
}
