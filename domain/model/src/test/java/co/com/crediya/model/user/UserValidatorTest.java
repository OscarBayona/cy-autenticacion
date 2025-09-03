package co.com.crediya.model.user;

import co.com.crediya.model.constants.SalaryLimits;
import co.com.crediya.model.exceptions.user.InvalidUserException;
import co.com.crediya.model.user.validators.UserValidator;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidatorTest {

    @Test
    void mustSuccessSalaryWithinRange() {
        User user = new User();
        user.setSalaryBase(new BigDecimal("1000000"));
        assertDoesNotThrow(() -> UserValidator.validateUser(user));
    }

    @Test
    void mustFailSalaryNull() {
        User user = new User();
        user.setSalaryBase(null);

        StepVerifier.create(UserValidator.validateUser(user))
                .expectError(InvalidUserException.class)
                .verify();
    }

    @Test
    void mustFailSalaryBelowMin() {
        User user = new User();
        user.setSalaryBase(SalaryLimits.MIN.subtract(BigDecimal.ONE));

        StepVerifier.create(UserValidator.validateUser(user))
                .expectError(InvalidUserException.class)
                .verify();
    }

    @Test
    void mustFailSalaryAboveMax() {
        User user = new User();
        user.setSalaryBase(SalaryLimits.MAX.add(BigDecimal.ONE));

        StepVerifier.create(UserValidator.validateUser(user))
                .expectError(InvalidUserException.class)
                .verify();
    }
}