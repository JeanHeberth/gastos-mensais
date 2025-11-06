package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.repository.ParcelaRepository;
import br.com.gastosmensais.util.TestDataFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ParcelaServiceTest {

    private ParcelaService parcelaService;
    private ParcelaRepository parcelaRepository;

    @BeforeEach
    void setUp() {
        parcelaRepository = Mockito.mock(ParcelaRepository.class);
        parcelaService = new ParcelaService(parcelaRepository);
    }

    @Test
    void deveGerarParcelasCorretamente() {
        var gasto = TestDataFactory.criarGastoRequestPadrao();

        List<ParcelaResponseDTO> parcelas = parcelaService.gerarEGuardarParcelas(gasto, "Gastos123");

        assertThat(parcelas).hasSize(3);
        assertThat(parcelas.get(0).valor()).isEqualTo(new BigDecimal("2000.00"));
        assertThat(parcelas.get(0).dataVencimento()).isEqualTo("2025-11-06");
        assertThat(parcelas.get(2).dataVencimento()).isEqualTo("2026-01-06");
    }

    @Test
    void deveGerarUmaParcelaQuandoNaoEspecificado() {
        var gasto = TestDataFactory.criarGastoRequestPadrao();

        List<ParcelaResponseDTO> parcelas = parcelaService.gerarEGuardarParcelas(gasto, "Gastos123");

        assertThat(parcelas).hasSize(3);
        assertThat(parcelas.get(0).valor()).isEqualTo(new BigDecimal("2000.00"));
        assertThat(parcelas.get(0).dataVencimento()).isEqualTo("2025-11-06");
    }
}
