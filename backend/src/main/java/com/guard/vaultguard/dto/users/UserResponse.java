package com.guard.vaultguard.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // ignore null
public class UserResponse {
    private String username;
    private String userJwt;
    private String jwtType;
    private String role;

    public static Map<String, Object> buildUserResponse(UserResponse res, String message) {
        return Map.of(
                "user", res,
                "message", message,
                "TimeStamp", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
    }
}
