package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entities.UsuarioData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UsuarioReactiveRepository extends ReactiveCrudRepository<UsuarioData, Long>, ReactiveQueryByExampleExecutor<UsuarioData> {
    Mono<UsuarioData> findByCorreoElectronico(String correo);
}
