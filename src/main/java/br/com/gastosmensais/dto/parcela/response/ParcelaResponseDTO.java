package br.com.gastosmensais.dto.parcela.response;

import br.com.gastosmensais.entity.Parcela;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParcelaResponseDTO(
        Integer numero,
        BigDecimal valor,
        LocalDate dataVencimento,
        String gastoId
) {
    public static ParcelaResponseDTO fromRequest(Parcela parcela) {
        return new ParcelaResponseDTO(
                parcela.getNumero(),
                parcela.getValor(),
                parcela.getDataVencimento(),
                parcela.getGastoId()
        );
    }
}
