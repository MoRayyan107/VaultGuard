package com.guard.vaultguard.controllers.auth;

import com.guard.vaultguard.dto.users.UserRequest;
import com.guard.vaultguard.dto.users.UserResponse;
import com.guard.vaultguard.exceptions.InvalidUserDataException;
import com.guard.vaultguard.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserRequest userRequest) {
        UserResponse res = userService.verifyUserOnLogin(userRequest);

        return ResponseEntity.ok(buildResponse(res, "Login Successful"));

    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserRequest userRequest) {

        UserResponse res = userService.registerUser(userRequest);

        return ResponseEntity.ok(buildResponse(res, "Registration Successful"));
    }

    private Map<String, Object> buildResponse(UserResponse res, String message) {
        return Map.of(
                "user", res,
                "message", message,
                "TimeStamp", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
    }


}
