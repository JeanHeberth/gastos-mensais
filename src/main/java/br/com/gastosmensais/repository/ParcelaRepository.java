package br.com.gastosmensais.repository;

import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.entity.Parcela;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface ParcelaRepository extends MongoRepository<Parcela, String> {
    List<Parcela> findByGastoId(String gastoId);


    List<Parcela> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);


    @Aggregation(pipeline = {
            "{ $match: { 'dataVencimento': { $gte: ?0, $lte: ?1 } } }",
            "{ $addFields: { gastoObjectId: { $toObjectId: '$gastoId' } } }", // 👈 converte string → ObjectId
            "{ $lookup: { from: 'gastos', localField: 'gastoObjectId', foreignField: '_id', as: 'gasto' } }",
            "{ $unwind: '$gasto' }",
            "{ $project: { numero: 1, valor: 1, dataVencimento: 1, gastoId: 1, " +
                    "descricao: '$gasto.descricao', categoria: '$gasto.categoria' } }"
    })
    List<ParcelaResponseDTO> findParcelasComGastoByDataVencimentoBetween(LocalDate inicio, LocalDate fim);
}