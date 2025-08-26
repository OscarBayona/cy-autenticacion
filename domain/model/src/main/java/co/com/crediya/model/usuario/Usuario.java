package co.com.crediya.model.usuario;
import co.com.crediya.model.contants.LimitesSalarioBase;
import co.com.crediya.model.exceptions.NegocioException;
import co.com.crediya.model.exceptions.ValidacionException;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Usuario {

    private Long idUsuario;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String correoElectronico;
    private Integer salarioBase;

    public void validar() {
        validarNombres(this.nombre);
        validarApellidos(this.apellido);
        validarCorreoElectronico(this.correoElectronico);
        validarSalarioBase(this.salarioBase);
    }

    private static void validarNombres(String nombres) {
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new ValidacionException("El nombre del usuario es obligatorio.");
        }
    }

    private static void validarApellidos(String apellidos) {
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new ValidacionException("El apellido del usuario es obligatorio.");
        }
    }

    private static void validarCorreoElectronico(String correoElectronico) {
        if (correoElectronico == null || correoElectronico.trim().isEmpty()) {
            throw new ValidacionException("El correo electrónico del usuario es obligatorio.");
        }
        if (!correoElectronico.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ValidacionException("El formato del correo electrónico no es válido.");
        }
    }

    private static void validarSalarioBase(Integer salarioBase) {
        if (salarioBase == null || salarioBase < LimitesSalarioBase.MINIMO || salarioBase > LimitesSalarioBase.MAXIMO) {
            throw new NegocioException("El salario base es obligatorio y debe estar entre 0 y 15000000.");
        }
    }
}
