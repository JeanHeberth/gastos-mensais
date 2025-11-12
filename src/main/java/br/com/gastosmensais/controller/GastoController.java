package br.com.gastosmensais.controller;

import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.gasto.response.GastoResponseDTO;
import br.com.gastosmensais.service.GastoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/gastos")
@RequiredArgsConstructor
public class GastoController {

    private final GastoService gastoService;

    @PostMapping
    public ResponseEntity<GastoResponseDTO> criar(@RequestBody @Valid GastoRequestDTO request) {
        GastoResponseDTO response = gastoService.criarGastos(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GastoResponseDTO>> listarGastos() {
        return ResponseEntity.ok(gastoService.listarGastos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GastoResponseDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(gastoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastoResponseDTO> atualizar(@PathVariable String id, @RequestBody @Valid GastoRequestDTO request) {
        return ResponseEntity.ok(gastoService.atualizarGasto(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        gastoService.deletarGasto(id);
        return ResponseEntity.noContent().build();
    }
}
