package co.com.crediya.usecase.user;

import co.com.crediya.model.constants.SalaryLimits;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.TechnicalException;
import co.com.crediya.model.exceptions.user.InvalidUserException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class CreateUserUseCase {
    private final UserRepository userRepository;
    private final Logger logger = Logger.getLogger(CreateUserUseCase.class.getName());

    public Mono<User> execute(User user) {
        if (user == null) {
            return Mono.error(new RuntimeException("El usuario no puede ser nulo"));
        }
        try {
            validateUser(user);
        } catch (InvalidUserException e) {
            return Mono.error(e);
        }
        return userRepository.getByEmail(user.getEmail())
                .doOnSubscribe(sub -> logger.info("CreateUserUseCase: Verificando si existe el usuario con correo " + user.getEmail()))
                .hasElement()
                .doOnNext(exist -> logger.info("CreateUserUseCase: Usuario con correo " + user.getEmail() + (Boolean.TRUE.equals(exist) ? " existe" : " no existe")))
                .doOnError(error -> logger.severe("CreateUserUseCase: Error al verificar si el usuario con correo " + user.getEmail()))
                .flatMap(exist -> {
                    if (Boolean.TRUE.equals(exist)) {
                        return Mono.error(new BusinessException("El correo electrónico ya está registrado."));
                    }
                    return userRepository.createUser(user)
                            .doOnSubscribe(sub -> logger.info("CreateUserUseCase: Registrando usuario con correo " + user.getEmail()))
                            .doOnSuccess(createdUser -> logger.info("CreateUserUseCase: Usuario registrado con correo " + createdUser.getEmail()))
                            .doOnError(error -> {
                                throw new TechnicalException(error.getMessage());
                            });
                });
    }

    public void validateUser(User user) {
        validateSalaryBase(user.getSalaryBase());
    }

    private static void validateSalaryBase(BigDecimal salaryBase) {
        if (salaryBase == null || salaryBase.compareTo(SalaryLimits.MIN) < 0 || salaryBase.compareTo(SalaryLimits.MAX) > 0) {
            throw new InvalidUserException("El salario base es obligatorio y debe estar entre "+SalaryLimits.MIN+" y "+SalaryLimits.MAX+".");
        }
    }
}
