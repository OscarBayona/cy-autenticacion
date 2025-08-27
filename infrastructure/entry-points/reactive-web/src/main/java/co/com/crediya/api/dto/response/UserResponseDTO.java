package co.com.crediya.api.dto.response;

import java.math.BigDecimal;

public record UserResponseDTO(
        String firstName,
        String lastName,
        String birthDate,
        String address,
        String phone,
        String email,
        BigDecimal salaryBase
){
}
