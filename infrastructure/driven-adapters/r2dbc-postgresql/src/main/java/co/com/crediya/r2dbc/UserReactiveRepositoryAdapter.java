package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.entities.UserData;
import co.com.crediya.r2dbc.mapper.UserEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserReactiveRepositoryAdapter implements UserRepository {

    private final UserReactiveRepository repository;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<User> createUser(User user) {
        final UserData userData = UserEntityMapper.toData(user);
        return transactionalOperator.transactional(
                repository.save(userData)
                        .doOnSubscribe(s -> log.trace(
                                "RegistrandoUsuario - Guardando usuario en la base de datos: {}",
                                userData.getEmail()))
                        .doOnNext(saved -> log.debug(
                                "RegistrandoUsuario - Usuario guardado con ID: {}",
                                saved.getId()))
                        .doOnError(e -> log.error(
                                "RegistrandoUsuario - Error al guardar el usuario {}: {}",
                                userData.getEmail(), e.getMessage()))
                        .flatMap(this::mapToUsuarioSimple)
        );
    }

    @Override
    public Mono<User> getByEmail(String correoElectronico) {
        return repository.findByEmail(correoElectronico)
                .doOnSubscribe(s -> log.trace(
                        "BuscarPorCorreoElectronico - Buscando usuario: {}", correoElectronico))
                .doOnNext(u -> log.debug(
                        "BuscarPorCorreoElectronico - Usuario encontrado con ID: {}", u.getId()))
                .doOnError(e -> log.error(
                        "BuscarPorCorreoElectronico - Error buscando {}: {}",
                        correoElectronico, e.getMessage()))
                .flatMap(this::mapToUsuarioSimple);
    }

    @Override
    public Mono<User> findByIdentityDocument(String identityDocument) {
        return repository.findByIdentityDocument(identityDocument)
                .flatMap(this::mapToUsuarioSimple);
    }

    private Mono<User> mapToUsuarioSimple(UserData data) {
        if (data == null) return Mono.empty();
        User user = UserEntityMapper.toEntity(data);
        return Mono.just(user);
    }

}
