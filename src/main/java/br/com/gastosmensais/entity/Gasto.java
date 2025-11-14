package br.com.gastosmensais.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "gastos")
public class Gasto {

    @Id
    private String id;
    private String usuarioId;
    private String descricao;
    private BigDecimal valorTotal;
    private String categoria;
    private String tipoPagamento;
    private Integer parcelas;
    private LocalDateTime dataCompra;

}