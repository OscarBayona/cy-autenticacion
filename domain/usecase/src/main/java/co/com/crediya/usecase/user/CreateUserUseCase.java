package co.com.crediya.usecase.user;

import co.com.crediya.model.constants.SalaryLimits;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.TechnicalException;
import co.com.crediya.model.exceptions.user.InvalidUserException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.user.validators.UserValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateUserUseCase {
    private final UserRepository userRepository;

    public Mono<User> execute(User user) {
        return UserValidator.validateUser(user)
                .then(Mono.defer(() ->
                        userRepository.getByEmail(user.getEmail()) // recién acá usamos user
                                .hasElement()
                                .flatMap(exist -> {
                                    if (Boolean.TRUE.equals(exist)) {
                                        return Mono.error(new BusinessException("El correo electrónico ya está registrado."));
                                    }
                                    return userRepository.createUser(user)
                                            .onErrorResume(error -> Mono.error(new TechnicalException(error.getMessage())));
                                })
                ));
    }

}
