package br.com.gastosmensais.integration;

import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.dto.gasto.response.GastoResponseDTO;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.repository.GastoRepository;
import br.com.gastosmensais.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class GastoRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private GastoRepository gastoRepository;

    @BeforeEach
    void limpaCollection() {
        gastoRepository.deleteAll();
    }


    @Test
    void deveSalvarGasto() {
        var gasto = TestDataFactory.criarGastoEntityPadrao();
        gastoRepository.save(gasto);

        var gastoSalvo = GastoResponseDTO.fromRequest(gasto);

        assertThat(gastoSalvo.descricao()).isEqualTo(gasto.getDescricao());
        assertThat(gastoSalvo.valorTotal()).isEqualTo(gasto.getValorTotal());
        assertThat(gastoSalvo.categoria()).isEqualTo(gasto.getCategoria());
        assertThat(gastoSalvo.tipoPagamento()).isEqualTo(gasto.getTipoPagamento());
        assertThat(gastoSalvo.parcelas()).isEqualTo(gasto.getParcelas());
        assertThat(gastoSalvo.dataCompra()).isEqualTo(gasto.getDataCompra());
    }

    @Test
    void deveBuscarGastoPorId() {
        var gasto = TestDataFactory.criarGastoEntityPadrao();

        gastoRepository.save(gasto);

        var gastoSalvo = gastoRepository.findById(gasto.getId()).get();

        assertThat(gastoSalvo).isNotNull();
    }
}
