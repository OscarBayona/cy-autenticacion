package co.com.crediya.api.config;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.TechnicalException;
import co.com.crediya.model.exceptions.user.InvalidUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // asegúrate de que corra antes que otros handlers
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final HandlerStrategies strategies = HandlerStrategies.withDefaults();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        log.error("Error capturado: {}", ex.getMessage(), ex);

        HttpStatus status;
        String customMessage;

        if (ex instanceof BusinessException) {
            status = HttpStatus.CONFLICT; // 409
            customMessage = "Error de negocio: " + ex.getMessage();
        } else if (ex instanceof InvalidUserException) {
            status = HttpStatus.BAD_REQUEST; // 400
            customMessage = "Error de validación: " + ex.getMessage();
        } else if (ex instanceof TechnicalException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
            customMessage = "Error técnico en el sistema, contacte soporte.";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            customMessage = "Error inesperado: " + ex.getMessage();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", customMessage);
        body.put("exception", ex.getClass().getSimpleName());
        body.put("path", exchange.getRequest().getPath().value());

        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .flatMap(resp -> resp.writeTo(exchange, new ServerResponse.Context() {
                    @Override
                    public List<HttpMessageWriter<?>> messageWriters() {
                        return strategies.messageWriters();
                    }
                    @Override
                    public List<ViewResolver> viewResolvers() {
                        return strategies.viewResolvers();
                    }
                }))
                .onErrorResume(writeErr -> {
                    log.warn("Fallo al escribir la respuesta de error: {}", writeErr.getMessage(), writeErr);
                    return Mono.empty();
                });
    }
}