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

@RequiredArgsConstructor
public class CreateUserUseCase {
    private final UserRepository userRepository;

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
                .hasElement()
                .flatMap(exist -> {
                    if (Boolean.TRUE.equals(exist)) {
                        return Mono.error(new BusinessException("El correo electrónico ya está registrado."));
                    }
                    return userRepository.createUser(user)
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
