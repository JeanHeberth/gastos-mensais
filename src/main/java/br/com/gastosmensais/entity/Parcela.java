package br.com.gastosmensais.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private String descricao;
    private String categoria;




}
