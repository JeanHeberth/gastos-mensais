package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.entity.Parcela;
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

    /**
     * Gera e salva as parcelas de um gasto.
     * Agora inclui o usuarioId herdado do Gasto.
     */
    public List<Parcela> gerarEGuardarParcelas(GastoRequestDTO gasto, String gastoId, String usuarioId) {

        int totalParcelas = gasto.parcelas() != null ? gasto.parcelas() : 1;

        BigDecimal valorParcela = gasto.valorTotal()
                .divide(BigDecimal.valueOf(totalParcelas), 2, RoundingMode.HALF_UP);

        List<Parcela> parcelas = IntStream.rangeClosed(1, totalParcelas)
                .mapToObj(numero -> Parcela.builder()
                        .numero(numero)
                        .valor(valorParcela)
                        .dataVencimento(gasto.dataCompra().toLocalDate().plusMonths(numero - 1))
                        .gastoId(gastoId)
                        .usuarioId(usuarioId) // 🔐 VÍNCULO COM DONO DO GASTO
                        .descricao(gasto.descricao())
                        .categoria(gasto.categoria())
                        .build()
                ).toList();

        parcelaRepository.saveAll(parcelas);

        return parcelas;
    }

    /**
     * Lista parcelas por ID do gasto
     */
    public List<ParcelaResponseDTO> listarParcelasPorGasto(String gastoId, String usuarioId) {
        return parcelaRepository.findByGastoId(gastoId)
                .stream()
                .filter(p -> p.getUsuarioId().equals(usuarioId)) // 🔐 GARANTIA EXTRA
                .map(ParcelaResponseDTO::fromRequest)
                .toList();
    }

    /**
     * Método alternativo (mantido por compatibilidade)
     */
    public List<ParcelaResponseDTO> buscarPorGastoId(String gastoId, String usuarioId) {
        return parcelaRepository.findByGastoId(gastoId)
                .stream()
                .filter(p -> p.getUsuarioId().equals(usuarioId)) // 🔐 GARANTIA EXTRA
                .map(ParcelaResponseDTO::fromRequest)
                .collect(Collectors.toList());
    }

    /**
     * Busca parcelas do usuário por mês → usado no Dashboard
     */
    public List<ParcelaResponseDTO> buscarPorMes(YearMonth mes, String usuarioId) {

        LocalDate inicio = mes.atDay(1);
        LocalDate fim = mes.atEndOfMonth();

        return parcelaRepository
                .findByUsuarioIdAndDataVencimentoBetween(
                        usuarioId,
                        inicio,
                        fim
                ).stream()
                .map(ParcelaResponseDTO::fromRequest)
                .toList();
    }

    /**
     * Atualiza uma parcela específica
     */
    public ResponseEntity<ParcelaResponseDTO> atualizarParcela(String id, ParcelaResponseDTO parcelaAtualizada, String usuarioId) {

        Parcela parcela = parcelaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        // 🔐 Garante que o usuário só atualiza as próprias parcelas
        if (!parcela.getUsuarioId().equals(usuarioId)) {
            return ResponseEntity.status(403).build();
        }

        parcela.setDescricao(parcelaAtualizada.descricao());
        parcela.setCategoria(parcelaAtualizada.categoria());
        parcela.setValor(parcelaAtualizada.valor());
        parcela.setDataVencimento(parcelaAtualizada.dataVencimento());

        Parcela salva = parcelaRepository.save(parcela);

        return ResponseEntity.ok(ParcelaResponseDTO.fromRequest(salva));
    }

    /**
     * Deleta parcela + segurança por usuario
     */
    public ResponseEntity<Void> deletarParcela(String id, String usuarioId) {

        Parcela parcela = parcelaRepository.findById(id)
                .orElse(null);

        if (parcela == null) {
            return ResponseEntity.notFound().build();
        }

        if (!parcela.getUsuarioId().equals(usuarioId)) {
            return ResponseEntity.status(403).build();
        }

        parcelaRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
