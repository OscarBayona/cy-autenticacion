package co.com.crediya.r2dbc;

import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.r2dbc.entities.UsuarioData;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.mapper.UsuarioEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UsuarioReactiveRepositoryAdapter implements UsuarioRepository {

    private final UsuarioReactiveRepository repository;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<Usuario> guardarUsuario(Usuario usuario) {
        final UsuarioData usuarioData = UsuarioEntityMapper.toData(usuario);
        return transactionalOperator.transactional(
                repository.save(usuarioData)
                        .doOnSubscribe(s -> log.trace(
                                "RegistrandoUsuario - Guardando usuario en la base de datos: {}",
                                usuarioData.getCorreoElectronico()))
                        .doOnNext(saved -> log.debug(
                                "RegistrandoUsuario - Usuario guardado con ID: {}",
                                saved.getId()))
                        .doOnError(e -> log.error(
                                "RegistrandoUsuario - Error al guardar el usuario {}: {}",
                                usuarioData.getCorreoElectronico(), e.getMessage()))
                        .flatMap(this::mapToUsuarioSimple)
        );
    }

    @Override
    public Mono<Usuario> buscarPorCorreoElectronico(String correoElectronico) {
        return repository.findByCorreoElectronico(correoElectronico)
                .doOnSubscribe(s -> log.trace(
                        "BuscarPorCorreoElectronico - Buscando usuario: {}", correoElectronico))
                .doOnNext(u -> log.debug(
                        "BuscarPorCorreoElectronico - Usuario encontrado con ID: {}", u.getId()))
                .doOnError(e -> log.error(
                        "BuscarPorCorreoElectronico - Error buscando {}: {}",
                        correoElectronico, e.getMessage()))
                .flatMap(this::mapToUsuarioSimple);
    }

    private Mono<Usuario> mapToUsuarioSimple(UsuarioData data) {
        if (data == null) return Mono.empty();
        Usuario usuario = UsuarioEntityMapper.toEntity(data);
        return Mono.just(usuario);
    }

}
