package br.com.gastosmensais.repository;

import br.com.gastosmensais.entity.Gasto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GastoRepository extends MongoRepository<Gasto, String> {
    List<Gasto> findByCategoria(String categoria);
}
