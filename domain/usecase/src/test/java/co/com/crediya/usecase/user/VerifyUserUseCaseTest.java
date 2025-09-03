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
import static org.mockito.Mockito.*;

class VerifyUserUseCaseTest {

    private UserRepository userRepository;
    private VerifyUserUseCase verifyUserUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        verifyUserUseCase = new VerifyUserUseCase(userRepository);
    }

    @Test
    void shouldReturnUserWhenExists() {
        String doc = "12345";
        User expectedUser = User.builder().idUser(1L).identityDocument(doc).firstName("Oscar").email("oscarb1924@gmail.com").build();

        when(userRepository.findByIdentityDocument(doc)).thenReturn(Mono.just(expectedUser));

        StepVerifier.create(verifyUserUseCase.execute(doc))
                .expectNext(expectedUser)
                .verifyComplete();

        verify(userRepository).findByIdentityDocument(doc);
    }

    @Test
    void shouldFailWhenDocumentIsNull() {
        StepVerifier.create(verifyUserUseCase.execute(null))
                .expectError(InvalidUserException.class)
                .verify();

        verify(userRepository, never()).findByIdentityDocument(anyString());
    }

    @Test
    void shouldFailWhenDocumentIsEmpty() {
        StepVerifier.create(verifyUserUseCase.execute("   "))
                .expectError(InvalidUserException.class)
                .verify();

        verify(userRepository, never()).findByIdentityDocument(anyString());
    }

    @Test
    void shouldFailWhenUserDoesNotExist() {
        String doc = "9999";
        when(userRepository.findByIdentityDocument(doc)).thenReturn(Mono.empty());

        StepVerifier.create(verifyUserUseCase.execute(doc))
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidUserException &&
                                throwable.getMessage().contains("Usuario no encontrado"))
                .verify();

        verify(userRepository).findByIdentityDocument(doc);
    }
}