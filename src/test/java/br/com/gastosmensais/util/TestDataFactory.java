package br.com.gastosmensais.util;



import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.entity.Gasto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestDataFactory {

    public static GastoRequestDTO criarGastoRequestPadrao() {
        return new GastoRequestDTO(
                "Supermercado",
                new BigDecimal("450.00"),
                "Alimentação",
                "Cartão",
                2,
                LocalDateTime.now()
        );
    }

    // Entidade simulada de saída (mock do repositório)
    public static Gasto criarGastoEntityPadrao() {
        Gasto gasto = new Gasto();
        gasto.setId("gasto-123");
        gasto.setDescricao("Notebook");
        gasto.setValorTotal(new BigDecimal("6000.00"));
        gasto.setCategoria("Tecnologia");
        gasto.setTipoPagamento("Cartão");
        gasto.setParcelas(3);
        gasto.setDataCompra(LocalDateTime.of(2025, 11, 6, 0, 0));
        return gasto;
    }
}

