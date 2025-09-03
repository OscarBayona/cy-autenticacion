package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> createUser(User user);
    Mono<User> getByEmail(String correo);
    Mono<User> findByIdentityDocument(String identityDocument);
}
