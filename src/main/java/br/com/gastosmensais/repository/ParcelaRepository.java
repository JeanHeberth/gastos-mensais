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
            // 1️⃣ Filtra por usuarioId
            "{ $match: { 'usuarioId': ?0 } }",

            // 2️⃣ Filtra por intervalo de datas
            "{ $match: { 'dataVencimento': { $gte: ?1, $lte: ?2 } } }",

            // 3️⃣ Converte o gastoId para ObjectId (caso seja necessário)
            "{ $addFields: { gastoObjectId: { $convert: { input: '$gastoId', to: 'objectId', onError: null, onNull: null } } } }",

            // 4️⃣ Join com gastos
            "{ $lookup: { from: 'gastos', localField: 'gastoObjectId', foreignField: '_id', as: 'gasto' } }",

            // 5️⃣ Desconstrói array
            "{ $unwind: { path: '$gasto', preserveNullAndEmptyArrays: true } }",

            // 6️⃣ Projeta campos
            "{ $project: { " +
                    "numero: 1, " +
                    "valor: 1, " +
                    "dataVencimento: 1, " +
                    "gastoId: 1, " +
                    "usuarioId: 1, " +
                    "descricao: { $ifNull: ['$gasto.descricao', '-'] }, " +
                    "categoria: { $ifNull: ['$gasto.categoria', '-'] } " +
                    "} }"
    })
    List<Parcela> findByUsuarioIdAndDataVencimentoBetween(
            String usuarioId,      // ?0
            LocalDate inicio,      // ?1
            LocalDate fim          // ?2
    );

    void deleteByGastoId(String id);
}