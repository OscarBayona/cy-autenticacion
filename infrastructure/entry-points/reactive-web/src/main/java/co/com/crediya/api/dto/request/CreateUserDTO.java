package co.com.crediya.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateUserDTO(

        @NotBlank(message = "El firstName no puede estar vacío")
        String firstName,

        @NotBlank(message = "El lastName no puede estar vacío")
        String lastName,

        LocalDate birthDate,
        String address,
        String phone,

        @NotBlank(message = "El correo electrónico no puede estar vacío")
        @Email(message = "El correo electrónico no tiene un formato válido")
        String email,

        @NotNull(message = "El salario base no puede ser nulo")
        BigDecimal salaryBase
) {}