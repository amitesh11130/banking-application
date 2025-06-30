package com.monocept.user.request;

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
public class UserDto {

    @NotEmpty(message = "UserName is required")
    private String userName;

    @NotEmpty(message = "Password is required")
    private String password;

    @Pattern(regexp = "SAVINGS|CURRENT", message = "Role must be either SAVINGS or CURRENT")
    private String accountType;

    @Column(unique = true)
    @Pattern(regexp = "\\d{16}", message = "AadharNumber must be 16 digit number")
    @NotEmpty(message = "AadharNumber is required")
    private String aadharNumber;

    @Column(unique = true)
    @Pattern(regexp = "\\d{10}", message = "ContactNumber must be 10 digit number")
    @NotEmpty(message = "ContactNumber is required")
    private String contactNumber;

    private String address;
    private Double balance;
    private boolean isActive;

}
