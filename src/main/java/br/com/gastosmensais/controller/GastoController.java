package br.com.gastosmensais.controller;

import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.gasto.response.GastoResponseDTO;
import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.service.GastoService;
import br.com.gastosmensais.service.ParcelaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gastos")
@RequiredArgsConstructor
public class GastoController {

    private final GastoService gastoService;
    private final ParcelaService parcelaService;

    @PostMapping
    public ResponseEntity<GastoResponseDTO> criar(@RequestBody @Valid GastoRequestDTO request) {
        GastoResponseDTO response = gastoService.criarGastos(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<GastoResponseDTO>> listarGastos() {
        List<GastoResponseDTO> gastos = gastoService.listarGastos();
        return ResponseEntity.ok(gastos);
    }


    @GetMapping("/gasto/{gastoId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ParcelaResponseDTO>> listarParcelasPorId(@PathVariable String gastoId) {
        List<ParcelaResponseDTO> parcelas = parcelaService.buscarPorGastoId(gastoId);
        return ResponseEntity.ok(parcelas);
    }

    @GetMapping("/{id}/parcelas")
    public ResponseEntity<List<ParcelaResponseDTO>> listarParcelasPorGasto(@PathVariable String id) {
        List<ParcelaResponseDTO> parcelas = parcelaService.listarParcelasPorGasto(id);
        return ResponseEntity.ok(parcelas);
    }
}
