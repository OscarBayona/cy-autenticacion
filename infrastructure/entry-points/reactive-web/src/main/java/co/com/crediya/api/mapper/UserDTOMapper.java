package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.request.CreateUserDTO;
import co.com.crediya.api.dto.response.UserResponseDTO;
import co.com.crediya.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface UserDTOMapper {

    UserResponseDTO toResponse(User user);

    User toModel(CreateUserDTO createUserDTO);
}