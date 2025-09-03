package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entities.UserData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserData, Long>, ReactiveQueryByExampleExecutor<UserData> {
    Mono<UserData> findByEmail(String correo);
    Mono<UserData> findByIdentityDocument(String identityDocument);
}
