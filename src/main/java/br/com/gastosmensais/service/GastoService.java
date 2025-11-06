package br.com.gastosmensais.service;

import br.com.gastosmensais.dto.gasto.request.GastoRequestDTO;
import br.com.gastosmensais.dto.gasto.response.GastoResponseDTO;
import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.entity.Gasto;
import br.com.gastosmensais.repository.GastoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<GastoResponseDTO> listarGastos() {
        List<Gasto> gastos = gastoRepository.findAll();
        return gastos.stream()
                .map(GastoResponseDTO::fromRequest)
                .toList();
    }
}



