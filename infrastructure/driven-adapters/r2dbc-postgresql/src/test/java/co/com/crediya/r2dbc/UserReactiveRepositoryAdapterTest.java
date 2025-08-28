package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.r2dbc.entities.UserData;
import co.com.crediya.r2dbc.mapper.UserEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @InjectMocks
    private UserReactiveRepositoryAdapter adapter;

    @Mock
    private UserReactiveRepository repository;

    @Mock
    private TransactionalOperator transactionalOperator;

    @Test
    void mustCreateUserSuccessfully() {
        User user = new User();
        user.setEmail("test@test.com");

        UserData userData = UserEntityMapper.toData(user);
        userData.setId(1L);

        when(repository.save(any(UserData.class))).thenReturn(Mono.just(userData));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));

        StepVerifier.create(adapter.createUser(user))
                .expectNextMatches(saved -> saved.getEmail().equals("test@test.com"))
                .verifyComplete();

        verify(repository).save(any(UserData.class));
    }

    @Test
    void mustReturnUserByEmail() {
        UserData data = new UserData();
        data.setId(1L);
        data.setEmail("exist@test.com");

        when(repository.findByEmail("exist@test.com")).thenReturn(Mono.just(data));

        StepVerifier.create(adapter.getByEmail("exist@test.com"))
                .expectNextMatches(u -> u.getEmail().equals("exist@test.com"))
                .verifyComplete();
    }
}
