package br.com.gastosmensais.dto.gasto.response;

import java.math.BigDecimal;
import java.util.Map;

public record ResumoMensalResponseDTO(
        Integer mes,
        Integer ano,
        BigDecimal totalGastos,
        Map<String, BigDecimal> porCategoria,
        Long quantidadeGastos
) {}
