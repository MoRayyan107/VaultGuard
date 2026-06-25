package com.guard.vaultguard.service;

import com.guard.vaultguard.dto.users.UserRequest;
import com.guard.vaultguard.dto.users.UserResponse;
import com.guard.vaultguard.entities.Users;
import com.guard.vaultguard.entities.enums.UserRole;
import com.guard.vaultguard.exceptions.DuplicateUsernameException;
import com.guard.vaultguard.exceptions.InvalidUserDataException;
import com.guard.vaultguard.repositories.UserRepository;
import com.guard.vaultguard.security.jwt.JwtUtil;
import com.guard.vaultguard.security.userSecurity.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class UserService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository repository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil)
    {
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public UserResponse registerUser(UserRequest request) {
        if (!isValidUserInputs(request)) throw new InvalidUserDataException("Username or Password is empty");

        Users newUser = Users.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(UserRole.USER) // default role is USER
                .build();

        Users savedUser = null;
        try{
            savedUser = userRepository.save(newUser);
        } catch (DataIntegrityViolationException de){
            log.error("[ERROR] Duplicated username error: {}", de.getMessage());
            throw new DuplicateUsernameException("Username already exists");
        }

        // register will not gen a token thats why the null
        return buildUserResponse(savedUser.getUsername(), savedUser.getRole().name(), null);
    }

    public UserResponse verifyUserOnLogin(UserRequest request) {
        if (!isValidUserInputs(request)) throw new InvalidUserDataException("Username or Password is empty");

        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserPrincipal authenticated = (UserPrincipal) auth.getPrincipal();

            String username = authenticated.getUsername();
            String role = "ROLE_"+authenticated.getRole().toUpperCase();
            String token = jwtUtil.generateToken(username, role);

            return buildUserResponse(username, role, token);
        } catch (BadCredentialsException e) {
            log.warn("[WARN] Authentication failed for user: {}", request.getUsername(), e);
            throw new InvalidUserDataException("Invalid username or password");
        }
    }

    private UserResponse buildUserResponse(String username, String role, String token){
        UserResponse.UserResponseBuilder res = UserResponse.builder()
                .username(username)
                .role(role);

        if (token != null)
            res.userJwt(token)
                    .jwtType("Bearer");

        return res.build();
    }

    private boolean isValidUserInputs(UserRequest request){
        if (StringUtils.hasText(request.getUsername()) && StringUtils.hasText(request.getPassword())) return true;

        // works if the inputs are empty or null
        log.error("[ERROR] Username or Password is empty");
        return false;
    }

}
