package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.entity.Parcela;
import br.com.gastosmensais.repository.GastoRepository;
import br.com.gastosmensais.repository.ParcelaRepository;
import br.com.gastosmensais.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ParcelaServiceTest {

    private ParcelaService parcelaService;
    private ParcelaRepository parcelaRepository;
    private GastoRepository gastoRepository;

    @BeforeEach
    void setUp() {
        parcelaRepository = Mockito.mock(ParcelaRepository.class);
        gastoRepository = Mockito.mock(GastoRepository.class);
        parcelaService = new ParcelaService(parcelaRepository);
    }

    @Test
    void deveGerarParcelasCorretamente() {
        // Arrange
        GastoRequestDTO gasto = TestDataFactory.criarGastoRequestPadrao();
        String gastoId = "Gasto123";
        String usuarioId = "UserABC";

        // Act
        List<Parcela> parcelas = parcelaService.gerarEGuardarParcelas(gasto, gastoId, usuarioId);

        // Assert
        assertThat(parcelas).hasSize(3);

        // Primeira parcela
        Parcela p1 = parcelas.get(0);
        assertThat(p1.getValor()).isEqualTo(new BigDecimal("2000.00"));
        assertThat(p1.getDataVencimento()).isEqualTo(LocalDate.of(2025, 11, 6));
        assertThat(p1.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(p1.getGastoId()).isEqualTo(gastoId);

        // Última parcela
        Parcela p3 = parcelas.get(2);
        assertThat(p3.getDataVencimento()).isEqualTo(LocalDate.of(2026, 1, 6));
    }

    @Test
    void deveGerarUmaParcelaQuandoNaoEspecificado() {
        // Arrange
        GastoRequestDTO gasto = TestDataFactory.criarGastoRequestSemParcelas(); // precisa retornar parcelas=null
        String gastoId = "Gasto123";
        String usuarioId = "UserABC";

        // Act
        List<Parcela> parcelas = parcelaService.gerarEGuardarParcelas(gasto, gastoId, usuarioId);

        // Assert
        assertThat(parcelas).hasSize(1);

        Parcela p1 = parcelas.get(0);
        assertThat(p1.getValor()).isEqualTo(gasto.valorTotal());
        assertThat(p1.getDataVencimento()).isEqualTo(gasto.dataCompra().toLocalDate());
        assertThat(p1.getUsuarioId()).isEqualTo(usuarioId);
    }
}
