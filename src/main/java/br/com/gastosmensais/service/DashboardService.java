package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.response.ResumoMensalResponseDTO;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.entity.Parcela;
import br.com.gastosmensais.repository.GastoRepository;
import br.com.gastosmensais.repository.ParcelaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ParcelaRepository parcelaRepository;
    private final GastoRepository gastoRepository;

    public ResumoMensalResponseDTO gerarResumoMensal(Integer mes, Integer ano) {
        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());

        List<Parcela> parcelasDoMes = parcelaRepository.findByDataVencimentoBetween(inicio, fim);

        BigDecimal totalMes = parcelasDoMes.stream()
                .map(Parcela::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Busca a categoria do gasto via gastoId
        Map<String, BigDecimal> porCategoria = parcelasDoMes.stream()
                .collect(Collectors.groupingBy(
                        p -> gastoRepository.findById(p.getGastoId())
                                .map(Gasto::getCategoria)
                                .orElse("Sem categoria"),
                        Collectors.reducing(BigDecimal.ZERO, Parcela::getValor, BigDecimal::add)
                ));

        long quantidade = parcelasDoMes.size();

        return new ResumoMensalResponseDTO(
                mes,
                ano,
                totalMes,
                porCategoria,
                quantidade
        );
    }
}