package br.com.gastosmensais.integration;

import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.repository.ParcelaRepository;
import br.com.gastosmensais.service.ParcelaService;
import br.com.gastosmensais.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ParcelaServiceIT extends AbstractIntegrationTest {

    @Autowired
    private ParcelaService parcelaService;

    @Autowired
    private ParcelaRepository parcelaRepository;


    @Test
    void deveGerarParcelasCorretamente() {
       GastoRequestDTO gasto = TestDataFactory.criarGastoRequestPadrao();

        var parcelas = parcelaService.gerarEGuardarParcelas(gasto, "Gastos123","usuarioID");
        var parcelasSalva = parcelaRepository.findByGastoId("Gastos123");

        assertThat(parcelas).hasSize(3);
        assertThat(parcelasSalva).hasSize(3);
        assertThat(parcelasSalva.getFirst().getGastoId()).isEqualTo("Gastos123");
    }
}
