package com.guard.vaultguard.controllers.auth;

import com.guard.vaultguard.dto.users.UserRequest;
import com.guard.vaultguard.dto.users.UserResponse;
import com.guard.vaultguard.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserRequest userRequest, HttpServletResponse response) {
        UserResponse res = userService.verifyUserOnLogin(userRequest, response);

        return ResponseEntity.ok(UserResponse.buildUserResponse(HttpStatus.OK, res, "Login Successful"));

    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserRequest userRequest) {

        UserResponse res = userService.registerUser(userRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.buildUserResponse(HttpStatus.CREATED, res, "Registration Successful"));
    }

    @PreAuthorize("hasRole('ROLE_ANALYST') or hasRole('ROLE_MANAGER')")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response) {
        UserResponse res = userService.logoutUser(response);

        return ResponseEntity.ok(UserResponse.buildUserResponse(HttpStatus.OK, res, "Logout Successful"));
    }

}
