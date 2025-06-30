package com.monocept.user.request;


import com.monocept.user.entity.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCredentialDto {

    @NotEmpty(message = "UserName is required")
    @Column(unique = true)
    private String userName;

    @NotEmpty(message = "Password is required")
    private String password;

    @Builder.Default
    @Pattern(regexp = "USER|ADMIN", message = "Role must be either USER or ADMIN")
    private String role = Role.USER.name();
}
