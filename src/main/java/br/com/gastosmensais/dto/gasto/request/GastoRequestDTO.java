package br.com.gastosmensais.dto.gasto.request;

import br.com.gastosmensais.entity.Gasto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GastoRequestDTO(
        String descricao,
        BigDecimal valorTotal,
        String categoria,
        String tipoPagamento,
        Integer parcelas,
        LocalDateTime dataCompra
)
{
    public static Gasto toEntity(GastoRequestDTO dto) {
        return Gasto.builder()
                .descricao(dto.descricao())
                .valorTotal(dto.valorTotal())
                .categoria(dto.categoria())
                .tipoPagamento(dto.tipoPagamento())
                .parcelas(dto.parcelas())
                .dataCompra(dto.dataCompra())
                .build();
    }
}
