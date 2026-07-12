package com.guard.vaultguard.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.guard.vaultguard.entities.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @JsonProperty("user_role")
    private UserRole userRole;

    @NotBlank
    private String email;
}
