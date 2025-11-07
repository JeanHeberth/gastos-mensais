package br.com.gastosmensais.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcela {

    @Id
    private String id;
    private Integer numero;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private String gastoId;
}
