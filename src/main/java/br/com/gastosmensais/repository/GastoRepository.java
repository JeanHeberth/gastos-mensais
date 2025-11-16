package br.com.gastosmensais.repository;

import br.com.gastosmensais.entity.Gasto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface GastoRepository extends MongoRepository<Gasto, String> {

    // 🔐 Multiusuário: todos os gastos do usuário
    List<Gasto> findAllByUsuarioId(String usuarioId);

    // 🔐 Multiusuário + período
    List<Gasto> findByUsuarioIdAndDataCompraBetween(String usuarioId,
                                                    LocalDate inicio,
                                                    LocalDate fim);
}
