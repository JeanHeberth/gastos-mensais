package br.com.gastosmensais.repository;

import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.entity.Gasto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GastoRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private GastoRepository gastoRepository;

    @Test
    void deveSalvarEGastoNoMongo() {
        Gasto gasto = new Gasto();
        gasto.setDescricao("Aluguel");
        gasto.setValorTotal(new BigDecimal("1200.00"));
        gasto.setCategoria("Moradia");
        gasto.setTipoPagamento("Pix");
        gasto.setParcelas(1);
        gasto.setDataCompra(LocalDateTime.now());

        Gasto salvo = gastoRepository.save(gasto);
        assertThat(salvo.getId()).isNotNull();
    }
}

