package co.com.crediya.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegistrarUsuarioDTO(

        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,

        @NotBlank(message = "El apellido no puede estar vacío")
        String apellido,
        LocalDate fechaNacimiento,
        String direccion,
        String telefono,

        @NotBlank(message = "El correo electrónico no puede estar vacío")
        @Email(message = "El correo electrónico no tiene un formato válido")
        String correoElectronico,

        @NotNull(message = "El salario base no puede ser nulo")
        Integer salarioBase
) {}