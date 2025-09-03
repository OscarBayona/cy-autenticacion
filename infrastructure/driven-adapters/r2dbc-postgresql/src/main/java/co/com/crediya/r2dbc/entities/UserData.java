package co.com.crediya.r2dbc.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Table("usuario")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
    @Id
    @Column("id")
    private Long id;

    @Column("nombre")
    private String firstName;

    @Column("apellido")
    private String lastName;

    @Column("fecha_nacimiento")
    private LocalDate birthDate;

    @Column("direccion")
    private String address;

    @Column("telefono")
    private String phone;

    @Column("correo_electronico")
    private String email;

    @Column("salario_base")
    private BigDecimal salaryBase;

    @Column("documento_identidad")
    private String identityDocument;
}
