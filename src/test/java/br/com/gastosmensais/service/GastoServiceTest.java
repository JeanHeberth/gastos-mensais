package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.gasto.response.GastoResponseDTO;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.repository.GastoRepository;
import br.com.gastosmensais.repository.ParcelaRepository;
import br.com.gastosmensais.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GastoServiceTest {

    @Mock
    private GastoRepository gastoRepository;

    @Mock
    private ParcelaRepository parcelaRepository;

    @InjectMocks
    private GastoService gastoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarGastoComParcelas() {
        // Arrange
        GastoRequestDTO dto = TestDataFactory.criarGastoRequestPadrao();
        Gasto gastoMock = TestDataFactory.criarGastoEntityPadrao();

        when(gastoRepository.save(any(Gasto.class))).thenReturn(gastoMock);
        when(parcelaRepository.saveAll(any())).thenReturn(List.of());

        // Act
        GastoResponseDTO response = gastoService.salvarGasto(dto).getBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("gasto-123");
        assertThat(response.descricao()).isEqualTo("Notebook");
        assertThat(response.valorTotal()).isEqualByComparingTo("6000.00");
    }
}
