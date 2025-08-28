package co.com.crediya.api;

import co.com.crediya.api.dto.request.CreateUserDTO;
import co.com.crediya.api.dto.response.UserResponseDTO;
import co.com.crediya.api.mapper.UserDTOMapper;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.TechnicalException;
import co.com.crediya.model.exceptions.user.InvalidUserException;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.CreateUserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, RouterRestTest.TestConfig.class})
@WebFluxTest
class RouterRestTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        CreateUserUseCase createUserUseCase() {
            return Mockito.mock(CreateUserUseCase.class);
        }
        @Bean
        public UserDTOMapper userDTOMapper() {
            return Mockito.mock(UserDTOMapper.class);
        }

        @Bean
        public RequestValidator requestValidator() {
            return Mockito.mock(RequestValidator.class);
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CreateUserUseCase createUserUseCase;

    @Autowired
    private UserDTOMapper userDTOMapper;

    @Autowired
    private RequestValidator requestValidator;


    private User user;

    @BeforeEach
    void setUp() {
        Handler handler = new Handler(requestValidator, createUserUseCase, userDTOMapper);
        RouterFunction<ServerResponse> route = new RouterRest().routerFunction(handler);
        webTestClient = WebTestClient.bindToRouterFunction(route).build();

        user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.parse("1995-08-24"))
                .address("Cra 123 #45-67")
                .phone("3001234567")
                .email("johndoe@mail.com")
                .salaryBase(new BigDecimal("2500000"))
                .build();
    }


    @Test
    void createUserShouldReturnCreatedUser() {

        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.parse("1995-08-24"))
                .address("Cra 123 #45-67")
                .phone("3001234567")
                .email("johndo2e2@mail.com")
                .salaryBase(new BigDecimal("2500000"))
                .build();

        CreateUserDTO request = new CreateUserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getAddress(),
                user.getPhone(),
                user.getEmail(),
                user.getSalaryBase()
        );

        UserResponseDTO responseDto = new UserResponseDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate().toString(),
                user.getAddress(),
                user.getPhone(),
                user.getEmail(),
                user.getSalaryBase()
        );


        when(requestValidator.validate(any(CreateUserDTO.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        when(userDTOMapper.toModel(request))
                .thenReturn(user);

        when(createUserUseCase.execute(any(User.class)))
                .thenReturn(Mono.just(user));

        when(userDTOMapper.toResponse(user))
                .thenReturn(responseDto);

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDTO.class)
                .value(response -> {
                            Assertions.assertThat(response.email()).isEqualTo("johndo2e2@mail.com");
                            Assertions.assertThat(response.firstName()).isEqualTo("John");
                        }
                );
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() {
        CreateUserDTO request = new CreateUserDTO(null, null, null, null, null, "bademail", null);

        when(requestValidator.validate(any(CreateUserDTO.class)))
                .thenReturn(Mono.error(new InvalidUserException("Datos inválidos")));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnBadRequestWhenEmailAlreadyExists() {
        CreateUserDTO request = new CreateUserDTO(
                user.getFirstName(), user.getLastName(), user.getBirthDate(),
                user.getAddress(), user.getPhone(), user.getEmail(), user.getSalaryBase()
        );

        when(requestValidator.validate(any(CreateUserDTO.class))).thenReturn(Mono.just(request));
        when(userDTOMapper.toModel(any(CreateUserDTO.class))).thenReturn(user);
        when(createUserUseCase.execute(any(User.class)))
                .thenReturn(Mono.error(new BusinessException("El correo electrónico ya está registrado.")));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnInternalServerErrorWhenTechnicalExceptionOccurs() {
        CreateUserDTO request = new CreateUserDTO(
                user.getFirstName(), user.getLastName(), user.getBirthDate(),
                user.getAddress(), user.getPhone(), user.getEmail(), user.getSalaryBase()
        );

        when(requestValidator.validate(any(CreateUserDTO.class))).thenReturn(Mono.just(request));
        when(userDTOMapper.toModel(any(CreateUserDTO.class))).thenReturn(user);
        when(createUserUseCase.execute(any(User.class)))
                .thenReturn(Mono.error(new TechnicalException("Error de base de datos")));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
    }

}