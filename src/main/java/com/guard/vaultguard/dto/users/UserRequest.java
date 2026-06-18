package com.guard.vaultguard.dto.users;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class UserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
