package br.com.gastosmensais.dto.parcela.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParcelaResponseDTO(
        Integer numero,
        BigDecimal valor,
        LocalDate dataVencimento,
        String gastoId
) {
}
