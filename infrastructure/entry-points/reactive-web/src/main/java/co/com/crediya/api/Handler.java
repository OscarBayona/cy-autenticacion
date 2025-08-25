package co.com.crediya.api;

import co.com.crediya.api.dto.request.RegistrarUsuarioDTO;
import co.com.crediya.api.mapper.UsuarioDTOMapper;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.usecase.usuario.RegistrarUsuarioUseCase;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RequestValidador validador;
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final UsuarioDTOMapper usuarioDTOMapper;


    public Mono<ServerResponse> listenRegistrarUsuario(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(RegistrarUsuarioDTO.class)
                .flatMap(validador::validar)
                .map(usuarioDTOMapper::toModel)
                .flatMap(registrarUsuarioUseCase::execute)
                .flatMap(saveUser->ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(usuarioDTOMapper.toResponse(saveUser)));
    }
}
