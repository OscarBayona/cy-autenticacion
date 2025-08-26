package co.com.crediya.usecase.usuario;

import co.com.crediya.model.exceptions.NegocioException;
import co.com.crediya.model.exceptions.TecnicaException;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {
    private final UsuarioRepository usuarioRepository;
    private final Logger logger = Logger.getLogger(RegistrarUsuarioUseCase.class.getName());

    public Mono<Usuario> execute(Usuario usuario) {
        if (usuario == null) {
            return Mono.error(new RuntimeException("El usuario no puede ser nulo"));
        }
        usuario.validar();
        return usuarioRepository.buscarPorCorreoElectronico(usuario.getCorreoElectronico())
                .doOnSubscribe(sub -> logger.info("RegistrarUsuarioUseCase: Verificando si existe el usuario con correo " + usuario.getCorreoElectronico()))
                .hasElement()
                .doOnNext(existe -> logger.info("RegistrarUsuarioUseCase: Usuario con correo " + usuario.getCorreoElectronico() + (Boolean.TRUE.equals(existe) ? " existe" : " no existe")))
                .doOnError(error -> logger.severe("RegistrarUsuarioUseCase: Error al verificar si el usuario con correo " + usuario.getCorreoElectronico()))
                .flatMap(existe -> {
                    if (Boolean.TRUE.equals(existe)) {
                        return Mono.error(new NegocioException("El correo electrónico ya está registrado."));
                    }
                    return usuarioRepository.guardarUsuario(usuario)
                            .doOnSubscribe(sub -> logger.info("RegistrarUsuarioUseCase: Registrando usuario con correo " + usuario.getCorreoElectronico()))
                            .doOnSuccess(usuarioGuardado -> logger.info("RegistrarUsuarioUseCase: Usuario registrado con ID " + usuarioGuardado.getIdUsuario()))
                            .doOnError(error -> {
                                throw new TecnicaException(error.getMessage());
                            });
                });
    }
}
