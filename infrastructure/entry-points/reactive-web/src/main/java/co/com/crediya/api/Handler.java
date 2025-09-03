package co.com.crediya.api;

import co.com.crediya.api.constants.swagger.UserDocApi;
import co.com.crediya.api.dto.request.CreateUserDTO;
import co.com.crediya.api.dto.response.UserResponseDTO;
import co.com.crediya.api.mapper.UserDTOMapper;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.user.InvalidUserException;
import co.com.crediya.usecase.user.CreateUserUseCase;
import co.com.crediya.usecase.user.VerifyUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
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

    private final VerifyUserUseCase verifyUserUseCase;

    @Operation(
            operationId = "verifyUser",
            summary = "Verificar existencia de usuario por documento de identidad",
            description = "Este endpoint permite verificar si un usuario existe en el sistema a partir de su número de documento. " +
                    "Devuelve la información del usuario en caso de existir o un error en caso contrario.",
            parameters = {
                    @Parameter(
                            name = "document",
                            description = "Número de documento de identidad del usuario a verificar",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", example = "1234567890")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario encontrado correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida (ejemplo: documento vacío o nulo)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Documento de identidad no puede ser vacío\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Usuario no encontrado con el documento: 999999\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"error\": \"Detalle del error interno\" }")
                            )
                    )
            }
    )
    public Mono<ServerResponse> verifyUser(ServerRequest serverRequest) {
        return Mono.fromCallable(() -> serverRequest.pathVariable("document"))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .switchIfEmpty(Mono.error(new InvalidUserException("Documento de identidad no puede ser vacio")))
                .doOnNext(doc -> log.trace("Petición recibida para verificar usuario con documento: {}", doc))
                .flatMap(verifyUserUseCase::execute)
                .doOnNext(user -> log.debug("Usuario encontrado correctamente: {}", user.getEmail()))
                .map(userDTOMapper::toResponse)
                .flatMap(dto -> ServerResponse.ok().bodyValue(dto))
                .switchIfEmpty(ServerResponse.notFound().build()) // si no hay usuario
                .onErrorResume(InvalidUserException.class,
                        e -> ServerResponse.badRequest().bodyValue(Map.of("error", e.getMessage())))
                .onErrorResume(RuntimeException.class,
                        e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue(Map.of("error", e.getMessage())));
    }
}
