package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.entity.Parcela;
import br.com.gastosmensais.repository.GastoRepository;
import br.com.gastosmensais.repository.ParcelaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ParcelaService {

    private final ParcelaRepository parcelaRepository;
    private final GastoRepository gastoRepository;


    public List<ParcelaResponseDTO> gerarEGuardarParcelas(GastoRequestDTO gasto, String gastoId) {
        int totalParcelas = gasto.parcelas() != null ? gasto.parcelas() : 1;
        BigDecimal valorParcela = gasto.valorTotal()
                .divide(BigDecimal.valueOf(totalParcelas), 2, RoundingMode.HALF_UP);

        List<Parcela> parcelas = IntStream.rangeClosed(1, totalParcelas)
                .mapToObj(numero -> Parcela.builder()
                        .numero(numero)
                        .valor(valorParcela)
                        .dataVencimento(gasto.dataCompra().toLocalDate().plusMonths(numero - 1))
                        .gastoId(gastoId)
                        .build())
                .toList();

        parcelaRepository.saveAll(parcelas);

        return parcelas.stream()
                .map(p -> new ParcelaResponseDTO(p.getNumero(), p.getValor(), p.getDataVencimento(), p.getGastoId(), p.getDescricao(), p.getCategoria()))
                .toList();
    }

    public List<ParcelaResponseDTO> listarParcelasPorGasto(String gastoId) {
        return parcelaRepository.findByGastoId(gastoId)
                .stream()
                .map(ParcelaResponseDTO::fromRequest)
                .toList();
    }

    public List<ParcelaResponseDTO> buscarPorGastoId(String gastoId) {
        return parcelaRepository.findByGastoId(gastoId)
                .stream()
                .map(ParcelaResponseDTO::fromRequest)
                .collect(Collectors.toList());
    }

    public List<ParcelaResponseDTO> buscarPorMes(YearMonth mes) {
        LocalDate inicio = mes.atDay(1);
        LocalDate fim = mes.atEndOfMonth();

        return parcelaRepository.findParcelasComGastoByDataVencimentoBetween(inicio, fim);
    }

    public ResponseEntity<ParcelaResponseDTO> atualizarParcela(String id, ParcelaResponseDTO parcelaAtualizada) {
        Parcela parcela = parcelaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));
        parcela.setDescricao(parcelaAtualizada.descricao());
        parcela.setCategoria(parcelaAtualizada.categoria());
        parcela.setValor(parcelaAtualizada.valor());
        parcela.setDataVencimento(parcelaAtualizada.dataVencimento());
        return ResponseEntity.ok(ParcelaResponseDTO.fromRequest(parcela));
    }

    public void deletarParcela(String id) {
        if (!parcelaRepository.existsById(id)) {
            ResponseEntity.notFound().build();
            return;
        }
        parcelaRepository.deleteById(id);
        ResponseEntity.noContent().build();
    }
}
