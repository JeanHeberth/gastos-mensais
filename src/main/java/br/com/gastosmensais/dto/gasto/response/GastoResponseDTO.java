package br.com.gastosmensais.dto.gasto.response;

import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.entity.Gasto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record GastoResponseDTO(
        String id,
        String descricao,
        BigDecimal valorTotal,
        String categoria,
        String tipoPagamento,
        Integer parcelas,
        LocalDateTime dataCompra
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
