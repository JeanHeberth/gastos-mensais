package br.com.gastosmensais.dto.gasto.request;

import br.com.gastosmensais.entity.Gasto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GastoRequestDTO(

        @NotNull(message = "A descrição é obrigatória.")
        @Size(max = 100, message = "A descrição deve ter no máximo 100 caracteres.")
        String descricao,

        @NotNull(message = "O valor total é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor total deve ser maior que zero.")
        BigDecimal valorTotal,

        @NotBlank(message = "A categoria é obrigatória.")
        String categoria,

        @NotBlank(message = "O tipo de pagamento é obrigatório.")
        String tipoPagamento,

        @NotNull(message = "O número de parcelas é obrigatório.")
        @Positive(message = "O número de parcelas deve ser maior que zero.")
        Integer parcelas,

        @NotNull(message = "A data de compra é obrigatória.")
        LocalDateTime dataCompra
) {
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
