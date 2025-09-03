package co.com.crediya.usecase.user;

import co.com.crediya.model.exceptions.user.InvalidUserException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class VerifyUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> execute(String identityDocument) {
        return Mono.justOrEmpty(identityDocument)
                .map(String::trim)
                .filter(doc -> !doc.isEmpty())
                .switchIfEmpty(Mono.error(new InvalidUserException("Documento de identidad no puede ser vacío")))
                .flatMap(userRepository::findByIdentityDocument)
                .switchIfEmpty(Mono.error(new InvalidUserException("Usuario no encontrado con el documento: " + identityDocument)));
    }
}