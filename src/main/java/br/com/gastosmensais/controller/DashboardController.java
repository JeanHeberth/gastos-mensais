package br.com.gastosmensais.controller;

import br.com.gastosmensais.config.UsuarioLogado;
import br.com.gastosmensais.dto.gasto.response.ResumoMensalResponseDTO;
import br.com.gastosmensais.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gastos/resumo")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    private String getUsuarioLogadoId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioLogado usuarioLogado)) {
            throw new IllegalStateException("Usuário não autenticado");
        }
        return usuarioLogado.getId();
    }

    @GetMapping
    public ResponseEntity<ResumoMensalResponseDTO> obterResumo(
            @RequestParam Integer mes,
            @RequestParam Integer ano) {

        String usuarioId = getUsuarioLogadoId();

        return ResponseEntity.ok(
                dashboardService.gerarResumoMensal(mes, ano, usuarioId)
        );
    }
}
