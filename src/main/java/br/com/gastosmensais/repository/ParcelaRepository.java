package br.com.gastosmensais.repository;

import br.com.gastosmensais.entity.Parcela;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ParcelaRepository extends MongoRepository<Parcela, String> {
    List<Parcela> findByGastoId(String gastoId);
}
