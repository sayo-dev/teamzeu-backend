package com.teamzeu.velo.user.dto;

import com.teamzeu.velo.common.enums.VELO_ROLE;
import jakarta.validation.constraints.*;

public record SignUpRequestDto (
    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    String fullName,

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    String password,

    @NotNull(message = "Role cannot be null")
    VELO_ROLE role,

    @NotEmpty(message = "Bio cannot be empty")
    @Size(min=10, max = 200, message = "Bio must be between 10 and 200 characters")
    String bio,

    @NotEmpty
    @Email
    String email){
}
