package br.com.gastosmensais.controller;

import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.service.ParcelaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/parcelas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ParcelaController {

    private final ParcelaService parcelaService;

    @GetMapping("/gasto/{gastoId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ParcelaResponseDTO>> listarParcelasPorId(@PathVariable String gastoId) {
        List<ParcelaResponseDTO> parcelas = parcelaService.buscarPorGastoId(gastoId);
        return ResponseEntity.ok(parcelas);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ParcelaResponseDTO>> listarParcelasPorMes(
            @RequestParam("mes")
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes
    ) {
        List<ParcelaResponseDTO> parcelas = parcelaService.buscarPorMes(mes);
        return ResponseEntity.ok(parcelas);
    }

    @GetMapping("/{id}/parcelas")
    public ResponseEntity<List<ParcelaResponseDTO>> listarParcelasPorGasto(@PathVariable String id) {
        List<ParcelaResponseDTO> parcelas = parcelaService.listarParcelasPorGasto(id);
        return ResponseEntity.ok(parcelas);
    }
}
