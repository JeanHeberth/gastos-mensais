package br.com.gastosmensais.repository;

import br.com.gastosmensais.dto.parcela.response.ParcelaResponseDTO;
import br.com.gastosmensais.entity.Parcela;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface ParcelaRepository extends MongoRepository<Parcela, String> {
    List<Parcela> findByGastoId(String gastoId);

    @Aggregation(pipeline = {
            // 1️⃣ Filtra as parcelas no intervalo do mês
            "{ $match: { 'dataVencimento': { $gte: ?0, $lte: ?1 } } }",

            // 2️⃣ Converte o gastoId (String) em ObjectId, com fallback de segurança
            "{ $addFields: { gastoObjectId: { $convert: { input: '$gastoId', to: 'objectId', onError: null, onNull: null } } } }",

            // 3️⃣ Faz o join com a coleção 'gastos'
            "{ $lookup: { from: 'gastos', localField: 'gastoObjectId', foreignField: '_id', as: 'gasto' } }",

            // 4️⃣ Desconstrói o array retornado pelo lookup
            "{ $unwind: { path: '$gasto', preserveNullAndEmptyArrays: true } }",

            // 5️⃣ Projeta os campos necessários para o frontend
            "{ $project: { " +
                    "numero: 1, " +
                    "valor: 1, " +
                    "dataVencimento: 1, " +
                    "gastoId: 1, " +
                    "descricao: { $ifNull: ['$gasto.descricao', '-'] }, " +
                    "categoria: { $ifNull: ['$gasto.categoria', '-'] } " +
                    "} }"
    })
    List<ParcelaResponseDTO> findParcelasComGastoByDataVencimentoBetween(LocalDate inicio, LocalDate fim);

    void deleteByGastoId(String id);
}