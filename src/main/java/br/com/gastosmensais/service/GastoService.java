package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.gasto.response.GastoResponseDTO;
import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.repository.GastoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GastoService {

    private final GastoRepository gastoRepository;
    private final ParcelaService parcelaService;


    public GastoResponseDTO criarGastos(GastoRequestDTO gastoRequestDTO) {

        Gasto gasto = gastoRequestDTO.toEntity(gastoRequestDTO);

        Gasto gastoSalvo = gastoRepository.save(gasto);

        List<ParcelaResponseDTO> parcelas = parcelaService.gerarEGuardarParcelas(gastoRequestDTO, gastoSalvo.getId());
        return new GastoResponseDTO(
                gastoSalvo.getId(),
                gastoSalvo.getDescricao(),
                gastoSalvo.getValorTotal(),
                gastoSalvo.getCategoria(),
                gastoSalvo.getTipoPagamento(),
                gastoSalvo.getParcelas(),
                gastoSalvo.getDataCompra(),
                parcelas
        );
    }

    public List<GastoResponseDTO> listarGastosPorPeriodo(Integer mes, Integer ano) {
        List<Gasto> gastos;

        if (mes != null && ano != null) {
            LocalDateTime inicio = LocalDateTime.of(ano, mes, 1, 0, 0);
            LocalDateTime fim = inicio.plusMonths(1);
            gastos = gastoRepository.findByDataCompraBetween(inicio, fim);
        } else {
            gastos = gastoRepository.findAll();
        }

        return gastos.stream()
                .map(GastoResponseDTO::fromRequest)
                .toList();
    }

    public List<GastoResponseDTO> listarGastos() {
        List<Gasto> gastos = gastoRepository.findAll();
        return gastos.stream()
                .map(GastoResponseDTO::fromRequest)
                .collect(Collectors.toList());
    }

    public GastoResponseDTO buscarPorId(String id) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto não encontrado"));
        return GastoResponseDTO.fromRequest(gasto);
    }

    public GastoResponseDTO atualizarGasto(String id, @Valid GastoRequestDTO request) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto não encontrado"));
        gasto.setDescricao(request.descricao());
        gasto.setValorTotal(request.valorTotal());
        gasto.setCategoria(request.categoria());
        gasto.setTipoPagamento(request.tipoPagamento());
        gasto.setParcelas(request.parcelas());
        gasto.setDataCompra(request.dataCompra());
        return GastoResponseDTO.fromRequest(gastoRepository.save(gasto));
    }

    public void deletarGasto(String id) {
        gastoRepository.deleteById(id);
    }
}



