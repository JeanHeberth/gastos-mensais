package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.gasto.response.GastoResponseDTO;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.entity.Parcela;
import br.com.gastosmensais.repository.GastoRepository;
import br.com.gastosmensais.repository.ParcelaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.gastosmensais.dto.gasto.request.GastoRequestDTO.toEntity;
import static br.com.gastosmensais.dto.gasto.response.GastoResponseDTO.fromRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class GastoService {

    private final GastoRepository gastoRepository;
    private final ParcelaRepository parcelaRepository;
    private final ParcelaService parcelaService;

    /**
     * Cria um novo gasto e gera as parcelas
     */
    public ResponseEntity<GastoResponseDTO> salvarGasto(GastoRequestDTO request, String usuarioId) {
        log.info("💾 Criando novo gasto: {} para usuário {}", request.descricao(), usuarioId);

        Gasto gasto = toEntity(request);
        gasto.setUsuarioId(usuarioId); // 🔹 vínculo com o usuário logado

        Gasto gastoSalvo = gastoRepository.save(gasto);

        List<Parcela> parcelas = parcelaService.gerarEGuardarParcelas(
                request, gastoSalvo.getId(), gastoSalvo.getUsuarioId());

        parcelaRepository.saveAll(parcelas);

        return ResponseEntity
                .status(201)
                .body(fromRequest(gastoSalvo));
    }

    /**
     * Atualiza um gasto existente e recalcula as parcelas
     */
    public ResponseEntity<GastoResponseDTO> atualizarGasto(String id, GastoRequestDTO request, String usuarioId) {
        log.info("✏️ Atualizando gasto ID: {} para usuário {}", id, usuarioId);

        Optional<Gasto> optionalGasto = gastoRepository.findById(id);
        if (optionalGasto.isEmpty()) {
            log.warn("⚠️ Gasto não encontrado: {}", id);
            return ResponseEntity.notFound().build();
        }

        Gasto gastoExistente = optionalGasto.get();

        // 🔒 Garante que o usuário só mexe no próprio gasto
        if (!usuarioId.equals(gastoExistente.getUsuarioId())) {
            log.warn("🚫 Usuário {} tentou atualizar gasto de outro usuário", usuarioId);
            return ResponseEntity.status(403).build();
        }

        gastoExistente.setDescricao(request.descricao());
        gastoExistente.setValorTotal(request.valorTotal());
        gastoExistente.setCategoria(request.categoria());
        gastoExistente.setTipoPagamento(request.tipoPagamento());
        gastoExistente.setParcelas(request.parcelas());
        gastoExistente.setDataCompra(request.dataCompra());

        Gasto gastoAtualizado = gastoRepository.save(gastoExistente);

        // 🔁 Recalcula parcelas SEM perder o vínculo de usuário
        parcelaRepository.deleteByGastoId(gastoAtualizado.getId());
        List<Parcela> novasParcelas = gerarParcelas(gastoAtualizado);
        parcelaRepository.saveAll(novasParcelas);

        log.info("✅ Parcelas recalculadas para o gasto ID: {}", id);

        return ResponseEntity.ok(fromRequest(gastoAtualizado));
    }

    public List<GastoResponseDTO> listarPorMes(YearMonth mes, String usuarioId) {

        LocalDate inicio = mes.atDay(1);
        LocalDate fim = mes.atEndOfMonth();

        List<Gasto> gastos = gastoRepository.findByUsuarioIdAndDataCompraBetween(
                usuarioId,
                inicio,
                fim
        );

        return gastos.stream()
                .map(GastoResponseDTO::fromRequest)
                .toList();
    }


    /**
     * Exclui um gasto e suas parcelas associadas
     */
    public ResponseEntity<Void> deletarGasto(String id, String usuarioId) {
        Optional<Gasto> optionalGasto = gastoRepository.findById(id);
        if (optionalGasto.isEmpty()) {
            log.warn("⚠️ Tentativa de exclusão de gasto inexistente: {}", id);
            return ResponseEntity.notFound().build();
        }

        Gasto gasto = optionalGasto.get();
        if (!usuarioId.equals(gasto.getUsuarioId())) {
            log.warn("🚫 Usuário {} tentou deletar gasto de outro usuário", usuarioId);
            return ResponseEntity.status(403).build();
        }

        parcelaRepository.deleteByGastoId(id);
        gastoRepository.deleteById(id);
        log.info("🗑 Gasto {} removido com sucesso para usuário {}.", id, usuarioId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Lista todos os gastos do usuário logado
     */
    public ResponseEntity<List<GastoResponseDTO>> listarTodos(String usuarioId) {
        log.info("📄 Listando gastos do usuário {}", usuarioId);

        List<Gasto> gastos = gastoRepository.findAllByUsuarioId(usuarioId);

        if (gastos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<GastoResponseDTO> responses = gastos.stream()
                .map(GastoResponseDTO::fromRequest)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Busca gasto por ID, garantindo que pertence ao usuário
     */
    public ResponseEntity<GastoResponseDTO> buscarPorId(String id, String usuarioId) {
        return gastoRepository.findById(id)
                .filter(g -> usuarioId.equals(g.getUsuarioId()))
                .map(gasto -> ResponseEntity.ok(fromRequest(gasto)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Gera as parcelas do gasto
     */
    private List<Parcela> gerarParcelas(Gasto gasto) {
        List<Parcela> parcelas = new ArrayList<>();

        BigDecimal valorParcela = gasto.getValorTotal()
                .divide(BigDecimal.valueOf(gasto.getParcelas()), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= gasto.getParcelas(); i++) {
            Parcela parcela = new Parcela();
            parcela.setNumero(i);
            parcela.setValor(valorParcela);
            parcela.setDataVencimento(LocalDate.from(gasto.getDataCompra().plusMonths(i - 1)));
            parcela.setGastoId(gasto.getId());
            parcela.setDescricao(gasto.getDescricao());
            parcela.setCategoria(gasto.getCategoria());

            // 🔐 ESSA LINHA É A CHAVE:
            parcela.setUsuarioId(gasto.getUsuarioId());

            parcelas.add(parcela);
        }

        return parcelas;
    }
}
