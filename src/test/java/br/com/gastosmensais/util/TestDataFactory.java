package br.com.gastosmensais.util;


import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.entity.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TestDataFactory {

    public static GastoRequestDTO criarGastoRequestPadrao() {
        return new GastoRequestDTO(
                "Notebook",
                new BigDecimal("6000.00"),
                "Tecnologia",
                "Cartão",
                3,
                LocalDateTime.of(2025, 11, 6, 0, 0)
        );
    }

    public static GastoRequestDTO criarGastoRequestSemParcelas() {
        return new GastoRequestDTO(
                "Compra Teste",
                new BigDecimal("1500.00"),
                "ALIMENTACAO",
                "CREDITO",
                null, // parcelas = null → será 1
                LocalDateTime.of(2025, 11, 6, 0, 0)
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

    // --- PARCELAS ---
    public static List<ParcelaResponseDTO> criarParcelasPadrao() {
        return List.of(
                new ParcelaResponseDTO(1, new BigDecimal("2000.00"), LocalDate.of(2025, 12, 6), "gasto-123", "Notebook", "Eletronico", "usuarioID"),
                new ParcelaResponseDTO(2, new BigDecimal("2000.00"), LocalDate.of(2026, 1, 6), "gasto-123", "Notebook", "Eletronico", "usuarioID"),
                new ParcelaResponseDTO(3, new BigDecimal("2000.00"), LocalDate.of(2026, 2, 6), "gasto-123", "Notebook", "Eletronico", "usuarioID")
        );
    }
}

