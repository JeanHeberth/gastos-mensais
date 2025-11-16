package br.com.gastosmensais.controller;

import br.com.gastosmensais.config.UsuarioLogado;
import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.service.ParcelaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/parcelas")
@RequiredArgsConstructor
public class ParcelaController {

    private final ParcelaService parcelaService;

    /**
     * Recupera o ID do usuário logado pelo SecurityContext
     */
    private String getUsuarioLogadoId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioLogado usuarioLogado)) {
            throw new IllegalStateException("Usuário não autenticado");
        }
        return usuarioLogado.getId();
    }

    /**
     * Buscar parcelas por ID do gasto
     */
    @GetMapping("/gasto/{gastoId}")
    public ResponseEntity<List<ParcelaResponseDTO>> listarPorGasto(@PathVariable String gastoId) {
        String usuarioId = getUsuarioLogadoId();

        List<ParcelaResponseDTO> parcelas = parcelaService.buscarPorGastoId(gastoId, usuarioId);

        if (parcelas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(parcelas);
    }

    /**
     * Buscar parcelas do mês do usuário logado
     * Exemplo de chamada: /parcelas/mes/2025-01
     */
    @GetMapping("/mes/{anoMes}")
    public ResponseEntity<List<ParcelaResponseDTO>> buscarPorMes(@PathVariable String anoMes) {

        String usuarioId = getUsuarioLogadoId();

        YearMonth mes = YearMonth.parse(anoMes);

        List<ParcelaResponseDTO> parcelas = parcelaService.buscarPorMes(mes, usuarioId);

        if (parcelas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(parcelas);
    }

    /**
     * Atualizar uma parcela específica
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParcelaResponseDTO> atualizarParcela(
            @PathVariable String id,
            @RequestBody ParcelaResponseDTO request
    ) {
        String usuarioId = getUsuarioLogadoId();

        return parcelaService.atualizarParcela(id, request, usuarioId);
    }

    /**
     * Deletar parcela
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarParcela(@PathVariable String id) {

        String usuarioId = getUsuarioLogadoId();

        return parcelaService.deletarParcela(id, usuarioId);
    }
}
