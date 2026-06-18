package com.guard.vaultguard.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // ignore null
public class UserResponse {
    private String username;
    private String userJwt;
    private String jwtType;
    private String role;
}
