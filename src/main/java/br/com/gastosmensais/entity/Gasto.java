package br.com.gastosmensais.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "gastos")
public class Gasto {

    @Id
    private String id;
    private String descricao;
    private BigDecimal valorTotal;
    private String categoria;
    private String tipoPagamento;
    private Integer parcelas;
    private LocalDateTime dataCompra;

    @Transient // não salva dentro do documento principal
    private List<Parcela> parcelasDetalhes;
}
