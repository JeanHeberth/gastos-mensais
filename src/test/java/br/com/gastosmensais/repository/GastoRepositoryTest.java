package br.com.gastosmensais.repository;

import br.com.gastosmensais.config.AbstractIntegrationTest;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.util.TestDataFactory;
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
        var gasto = TestDataFactory.criarGastoEntityPadrao();
        Gasto salvo = gastoRepository.save(gasto);
        assertThat(salvo.getId()).isNotNull();
    }
}

