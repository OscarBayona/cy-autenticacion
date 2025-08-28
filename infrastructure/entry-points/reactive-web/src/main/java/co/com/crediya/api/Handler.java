package co.com.crediya.api;

import co.com.crediya.api.constants.swagger.UserDocApi;
import co.com.crediya.api.dto.request.CreateUserDTO;
import co.com.crediya.api.dto.response.UserResponseDTO;
import co.com.crediya.api.mapper.UserDTOMapper;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.user.InvalidUserException;
import co.com.crediya.usecase.user.CreateUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RequestValidator validator;
    private final CreateUserUseCase createUserUseCase;
    private final UserDTOMapper userDTOMapper;


    @Operation(
            operationId = "CreateUser",
            summary = UserDocApi.SUMMARY_CREATE,
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateUserDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = UserDocApi.DESCRIPTION_CREATED,
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = UserResponseDTO.class
                                    )
                            )
                    )
            }
    )
    public Mono<ServerResponse> listenCreateUser(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(CreateUserDTO.class)
                .flatMap(validator::validate)
                .map(userDTOMapper::toModel)
                .flatMap(createUserUseCase::execute)
                .flatMap(saveUser->ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userDTOMapper.toResponse(saveUser)))
                .onErrorResume(BusinessException.class,
                        e -> ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(InvalidUserException.class,
                        e -> ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(RuntimeException.class,
                        e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage()));
    }
}
