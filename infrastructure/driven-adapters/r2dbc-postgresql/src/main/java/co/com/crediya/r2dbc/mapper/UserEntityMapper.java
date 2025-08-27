package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.user.User;
import co.com.crediya.r2dbc.entities.UserData;

public class UserEntityMapper {

    private UserEntityMapper() {}

    public static User toEntity(UserData data) {
        if (data == null) return null;
        return User.builder()
                .idUser(data.getId())
                .firstName(data.getFirstName())
                .lastName(data.getLastName())
                .birthDate(data.getBirthDate())
                .address(data.getAddress())
                .phone(data.getPhone())
                .email(data.getEmail())
                .salaryBase(data.getSalaryBase())
                .build();
    }

    public static UserData toData(User entity) {
        if (entity == null) return null;
        return UserData.builder()
                .id(entity.getIdUser())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .birthDate(entity.getBirthDate())
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .salaryBase(entity.getSalaryBase())
                .build();
    }
}