package br.com.gastosmensais.controller;

import br.com.gastosmensais.config.UsuarioLogado;
import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.gasto.response.GastoResponseDTO;
import br.com.gastosmensais.service.GastoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/gastos")
@RequiredArgsConstructor
public class GastoController {

    private final GastoService gastoService;

    private String getUsuarioLogadoId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioLogado usuarioLogado)) {
            throw new IllegalStateException("Usuário não autenticado");
        }
        return usuarioLogado.getId();
    }

    @PostMapping
    public ResponseEntity<GastoResponseDTO> salvar(@RequestBody @Valid GastoRequestDTO request) {
        String usuarioId = getUsuarioLogadoId();
        return gastoService.salvarGasto(request, usuarioId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastoResponseDTO> atualizar(@PathVariable String id,
                                                      @RequestBody @Valid GastoRequestDTO request) {
        String usuarioId = getUsuarioLogadoId();
        return gastoService.atualizarGasto(id, request, usuarioId);
    }

    @GetMapping
    public ResponseEntity<List<GastoResponseDTO>> listar() {
        String usuarioId = getUsuarioLogadoId();
        return gastoService.listarTodos(usuarioId);
    }


    @GetMapping("/{id}")
    public ResponseEntity<GastoResponseDTO> buscarPorId(@PathVariable String id) {
        String usuarioId = getUsuarioLogadoId();
        return gastoService.buscarPorId(id, usuarioId);
    }

    @GetMapping("/mes/{anoMes}")
    public ResponseEntity<List<GastoResponseDTO>> listarPorMes(@PathVariable String anoMes) {

        String usuarioId = getUsuarioLogadoId();

        // AnoMes vem no formato 2025-11
        YearMonth mes = YearMonth.parse(anoMes);

        List<GastoResponseDTO> gastos = gastoService.listarPorMes(mes, usuarioId);

        if (gastos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(gastos);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        String usuarioId = getUsuarioLogadoId();
        return gastoService.deletarGasto(id, usuarioId);
    }
}
