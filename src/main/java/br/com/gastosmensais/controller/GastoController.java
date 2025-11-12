package br.com.gastosmensais.controller;

import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.gasto.response.GastoResponseDTO;
import br.com.gastosmensais.service.GastoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<GastoResponseDTO> salvar(@RequestBody @Valid GastoRequestDTO request) {
        return gastoService.salvarGasto(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastoResponseDTO> atualizar(@PathVariable String id, @RequestBody @Valid GastoRequestDTO request) {
        return gastoService.atualizarGasto(id, request);
    }

    @GetMapping
    public ResponseEntity<List<GastoResponseDTO>> listar() {
        return gastoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GastoResponseDTO> buscarPorId(@PathVariable String id) {
        return gastoService.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        return gastoService.deletarGasto(id);
    }
}
