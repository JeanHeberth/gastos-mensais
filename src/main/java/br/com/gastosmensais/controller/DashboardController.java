package br.com.gastosmensais.controller;

import br.com.gastosmensais.dto.gasto.response.ResumoMensalResponseDTO;
import br.com.gastosmensais.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/gastos/resumo")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ResumoMensalResponseDTO> obterResumo(
            @RequestParam Integer mes,
            @RequestParam Integer ano) {

        return ResponseEntity.ok(dashboardService.gerarResumoMensal(mes, ano));
    }
}
