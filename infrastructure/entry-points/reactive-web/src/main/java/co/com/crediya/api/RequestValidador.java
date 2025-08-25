package co.com.crediya.api;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RequestValidador {

    private final Validator validador;

    public <T> Mono<T> validar(T dto) {
        return Mono.fromCallable(() -> {
            var errors = new BeanPropertyBindingResult(dto, dto.getClass().getName());
            validador.validate(dto, errors);
            if (errors.hasErrors()) throw new ValidationException(errors.toString());
            return dto;
        });
    }
}
