package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.r2dbc.entities.UsuarioData;

public class UsuarioEntityMapper {

    private UsuarioEntityMapper() {}

    public static Usuario toEntity(UsuarioData data) {
        if (data == null) return null;
        return Usuario.builder()
                .idUsuario(data.getId())
                .nombre(data.getNombre())
                .apellido(data.getApellido())
                .fechaNacimiento(data.getFechaNacimiento())
                .direccion(data.getDireccion())
                .telefono(data.getTelefono())
                .correoElectronico(data.getCorreoElectronico())
                .salarioBase(data.getSalarioBase())
                .build();
    }

    public static UsuarioData toData(Usuario entity) {
        if (entity == null) return null;
        return UsuarioData.builder()
                .id(entity.getIdUsuario())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .fechaNacimiento(entity.getFechaNacimiento())
                .direccion(entity.getDireccion())
                .telefono(entity.getTelefono())
                .correoElectronico(entity.getCorreoElectronico())
                .salarioBase(entity.getSalarioBase())
                .build();
    }
}