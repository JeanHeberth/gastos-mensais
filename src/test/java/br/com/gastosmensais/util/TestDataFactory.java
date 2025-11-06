package br.com.gastosmensais.util;



import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;

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
}

