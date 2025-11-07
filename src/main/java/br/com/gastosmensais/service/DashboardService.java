package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.response.ResumoMensalResponseDTO;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.repository.GastoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final GastoRepository gastoRepository;

    public ResumoMensalResponseDTO gerarResumoMensal(Integer mes, Integer ano) {
        LocalDateTime inicio = LocalDateTime.of(ano, mes, 1, 0, 0);
        LocalDateTime fim = inicio.plusMonths(1);

        List<Gasto> gastos = gastoRepository.findByDataCompraBetween(inicio, fim);

        BigDecimal total = gastos.stream()
                .map(Gasto::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> porCategoria = gastos.stream()
                .collect(Collectors.groupingBy(
                        Gasto::getCategoria,
                        Collectors.reducing(BigDecimal.ZERO, Gasto::getValorTotal, BigDecimal::add)
                ));

        return new ResumoMensalResponseDTO(
                mes,
                ano,
                total,
                porCategoria,
                (long) gastos.size()
        );
    }
}
