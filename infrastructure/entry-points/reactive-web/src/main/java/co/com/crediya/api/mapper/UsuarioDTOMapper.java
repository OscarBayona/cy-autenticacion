package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.request.RegistrarUsuarioDTO;
import co.com.crediya.api.dto.response.UsuarioResponseDTO;
import co.com.crediya.model.usuario.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface UsuarioDTOMapper {

    UsuarioResponseDTO toResponse(Usuario usuario);

    Usuario toModel(RegistrarUsuarioDTO registrarUsuarioDTO);
}