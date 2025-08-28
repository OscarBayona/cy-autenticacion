package co.com.crediya.model.user.validators;

import co.com.crediya.model.constants.SalaryLimits;
import co.com.crediya.model.exceptions.user.InvalidUserException;
import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class UserValidator {

    private UserValidator() {
        // Evitar instanciación
    }

    public static Mono<Void> validateUser(User user) {
        if (user == null) {
            return Mono.error(new InvalidUserException("El usuario no puede ser nulo."));
        }
        return validateSalaryBase(user.getSalaryBase());
    }

    private static Mono<Void> validateSalaryBase(BigDecimal salaryBase) {
        if (salaryBase == null
                || salaryBase.compareTo(SalaryLimits.MIN) < 0
                || salaryBase.compareTo(SalaryLimits.MAX) > 0) {
            return Mono.error(new InvalidUserException(
                    "El salario base es obligatorio y debe estar entre "
                            + SalaryLimits.MIN + " y " + SalaryLimits.MAX + "."
            ));
        }
        return Mono.empty();
    }
}
