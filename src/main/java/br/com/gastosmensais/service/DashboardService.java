package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.response.ResumoMensalResponseDTO;
import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
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

        List<ParcelaResponseDTO> parcelasDoMes = parcelaRepository.findParcelasComGastoByDataVencimentoBetween(inicio, fim);

        BigDecimal totalMes = parcelasDoMes.stream()
                .map(ParcelaResponseDTO::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Busca a categoria do gasto via gastoId
        Map<String, BigDecimal> porCategoria = parcelasDoMes.stream()
                .collect(Collectors.groupingBy(
                        p -> p.categoria(),
                        Collectors.reducing(BigDecimal.ZERO, ParcelaResponseDTO::valor, BigDecimal::add)
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