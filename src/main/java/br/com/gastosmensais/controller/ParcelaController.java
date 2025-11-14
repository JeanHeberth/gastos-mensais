package br.com.gastosmensais.controller;

import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.service.ParcelaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/parcelas")
@RequiredArgsConstructor
public class ParcelaController {

    private final ParcelaService parcelaService;

    // 🔹 Parcelas de um gasto específico
    @GetMapping("/gasto/{gastoId}")
    public ResponseEntity<List<ParcelaResponseDTO>> listarPorGasto(@PathVariable String gastoId) {
        return ResponseEntity.ok(parcelaService.buscarPorGastoId(gastoId));
    }

    // 🔹 Parcelas de um mês específico
    @GetMapping
    public ResponseEntity<List<ParcelaResponseDTO>> listarPorMes(
            @RequestParam("mes") @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes
    ) {
        return ResponseEntity.ok(parcelaService.buscarPorMes(mes));
    }

    // 🔹 Atualizar uma parcela
    @PutMapping("/{id}")
    public ResponseEntity<ParcelaResponseDTO> atualizar(
            @PathVariable String id,
            @RequestBody ParcelaResponseDTO dto
    ) {
        return ResponseEntity.ok(parcelaService.atualizarParcela(id, dto)).getBody();
    }

    // 🔹 Deletar uma parcela
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        parcelaService.deletarParcela(id);
        return ResponseEntity.noContent().build();
    }
}
