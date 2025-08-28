package co.com.crediya.model.user;

import co.com.crediya.model.constants.SalaryLimits;
import co.com.crediya.model.exceptions.user.InvalidUserException;
import co.com.crediya.model.user.validators.UserValidator;
import org.junit.jupiter.api.Test;

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
        assertThrows(InvalidUserException.class, () -> UserValidator.validateUser(user));
    }

    @Test
    void mustFailSalaryBelowMin() {
        User user = new User();
        user.setSalaryBase(SalaryLimits.MIN.subtract(BigDecimal.ONE));
        assertThrows(InvalidUserException.class, () -> UserValidator.validateUser(user));
    }

    @Test
    void mustFailSalaryAboveMax() {
        User user = new User();
        user.setSalaryBase(SalaryLimits.MAX.add(BigDecimal.ONE));
        assertThrows(InvalidUserException.class, () -> UserValidator.validateUser(user));
    }
}