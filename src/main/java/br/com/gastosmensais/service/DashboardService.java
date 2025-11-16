package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.response.ResumoMensalResponseDTO;
import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.entity.Parcela;
import br.com.gastosmensais.repository.ParcelaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ParcelaRepository parcelaRepository;

    public ResumoMensalResponseDTO gerarResumoMensal(Integer mes, Integer ano, String usuarioId) {

        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());

        // 🔍 BUSCA PARCELAS DO USUÁRIO NO MÊS
        List<Parcela> parcelas = parcelaRepository
                .findByUsuarioIdAndDataVencimentoBetween(usuarioId, inicio, fim);

        // 🔄 Convertendo para DTO — porque o frontend depende disso
        List<ParcelaResponseDTO> parcelasDTO = parcelas.stream()
                .map(ParcelaResponseDTO::fromRequest)
                .toList();

        // 💰 Total do mês
        BigDecimal totalMes = parcelasDTO.stream()
                .map(ParcelaResponseDTO::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 📊 Total por categoria
        Map<String, BigDecimal> porCategoria = parcelasDTO.stream()
                .collect(Collectors.groupingBy(
                        ParcelaResponseDTO::categoria,
                        Collectors.reducing(BigDecimal.ZERO, ParcelaResponseDTO::valor, BigDecimal::add)
                ));

        // 🔢 Quantidade de parcelas
        long quantidade = parcelasDTO.size();

        return new ResumoMensalResponseDTO(
                mes,
                ano,
                totalMes,
                porCategoria,
                quantidade
        );
    }
}
