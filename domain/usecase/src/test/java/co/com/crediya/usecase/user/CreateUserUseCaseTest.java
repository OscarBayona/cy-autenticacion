package co.com.crediya.usecase.user;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.TechnicalException;
import co.com.crediya.model.exceptions.user.InvalidUserException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateUserUseCaseTest {

    private UserRepository userRepository;
    private CreateUserUseCase createUserUseCase;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        createUserUseCase = new CreateUserUseCase(userRepository);
    }

    @Test
    void mustFailWhenUserIsNull() {
        StepVerifier.create(createUserUseCase.execute(null))
                .expectErrorMatches(error ->
                        error instanceof InvalidUserException &&
                                error.getMessage().equals("El usuario no puede ser nulo."))
                .verify();
    }

    @Test
    void mustFailWhenSalaryInvalid() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setSalaryBase(BigDecimal.valueOf(-1000));

        StepVerifier.create(createUserUseCase.execute(user))
                .expectError(InvalidUserException.class)
                .verify();
    }

    @Test
    void mustFailWhenEmailAlreadyExists() {
        User user = new User();
        user.setEmail("existente@test.com");
        user.setSalaryBase(BigDecimal.valueOf(1000000));

        when(userRepository.getByEmail("existente@test.com"))
                .thenReturn(Mono.just(user));

        StepVerifier.create(createUserUseCase.execute(user))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void mustCreateUserSuccessfully() {
        User user = new User();
        user.setEmail("nuevo@test.com");
        user.setSalaryBase(BigDecimal.valueOf(1000000)); // salario válido

        when(userRepository.getByEmail("nuevo@test.com"))
                .thenReturn(Mono.empty()); // no existe
        when(userRepository.createUser(any(User.class)))
                .thenReturn(Mono.just(user));

        StepVerifier.create(createUserUseCase.execute(user))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).createUser(user);
    }

    @Test
    void mustFailOnTechnicalError() {
        User user = new User();
        user.setEmail("nuevo@test.com");
        user.setSalaryBase(BigDecimal.valueOf(1000000)); // válido

        when(userRepository.getByEmail("nuevo@test.com"))
                .thenReturn(Mono.empty());
        when(userRepository.createUser(any(User.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(createUserUseCase.execute(user))
                .expectError(TechnicalException.class)
                .verify();
    }
}