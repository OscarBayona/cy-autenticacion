package co.com.crediya.api.dto.response;

public record UsuarioResponseDTO (
        Long idUsuario,
        String nombre,
        String apellido,
        String fechaNacimiento,
        String direccion,
        String telefono,
        String correoElectronico,
        Integer salarioBase
){
}
