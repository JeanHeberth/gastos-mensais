package br.com.gastosmensais.repository;

import br.com.gastosmensais.entity.Gasto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GastoRepository extends MongoRepository<Gasto, String> {

    List<Gasto> findByCategoria(String categoria);

    List<Gasto> findByDataCompraBetween(LocalDateTime inicio, LocalDateTime fim);

    // 🔐 Multiusuário: todos os gastos do usuário
    List<Gasto> findAllByUsuarioId(String usuarioId);

    // 🔐 Multiusuário + período
    List<Gasto> findByUsuarioIdAndDataCompraBetween(String usuarioId,
                                                    LocalDateTime inicio,
                                                    LocalDateTime fim);
}
