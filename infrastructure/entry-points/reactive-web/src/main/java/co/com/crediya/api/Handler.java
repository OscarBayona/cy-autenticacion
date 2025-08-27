package co.com.crediya.api;

import co.com.crediya.api.dto.request.CreateUserDTO;
import co.com.crediya.api.mapper.UserDTOMapper;
import co.com.crediya.usecase.user.CreateUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RequestValidador validator;
    private final CreateUserUseCase createUserUseCase;
    private final UserDTOMapper userDTOMapper;


    public Mono<ServerResponse> listenCreateUser(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(CreateUserDTO.class)
                .flatMap(validator::validate)
                .map(userDTOMapper::toModel)
                .flatMap(createUserUseCase::execute)
                .flatMap(saveUser->ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userDTOMapper.toResponse(saveUser)));
    }
}
