package br.com.gastosmensais.dto.gasto.response;

import br.com.gastosmensais.entity.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GastoResponseDTO(
        String id,
        String descricao,
        BigDecimal valorTotal,
        String categoria,
        String tipoPagamento,
        Integer parcelas,
        LocalDate dataCompra
) {

    public static GastoResponseDTO fromRequest(Gasto gasto) {
        return new GastoResponseDTO(
                gasto.getId(),
                gasto.getDescricao(),
                gasto.getValorTotal(),
                gasto.getCategoria(),
                gasto.getTipoPagamento(),
                gasto.getParcelas(),
                gasto.getDataCompra()
        );
    }
}
