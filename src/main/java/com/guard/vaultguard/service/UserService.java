package com.guard.vaultguard.service;

import com.guard.vaultguard.dto.users.UserRegisterRequest;
import com.guard.vaultguard.entities.Users;
import com.guard.vaultguard.entities.enums.UserRole;
import com.guard.vaultguard.exceptions.DuplicateUsernameException;
import com.guard.vaultguard.exceptions.InvalidUserDataException;
import com.guard.vaultguard.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder ) {
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users registerUser(UserRegisterRequest request){
        if (request.getUsername().isEmpty() || request.getPassword().isEmpty()){
            log.error("[ERROR] Username or Password is empty");
            throw new InvalidUserDataException("Username or Password is empty");
        }
        Users newUser = Users.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER) // default role is USER
                .build();

        log.info("[INFO] new User have been saved with username: {}", newUser.getUsername());

        Users savedUser = null;
        try{
            savedUser = userRepository.save(newUser);
        } catch (DataIntegrityViolationException de){
            log.error("[ERROR] Duplicated username error: {}", de.getMessage());
            if (de.getMessage().contains("username"))
                throw new DuplicateUsernameException("Username already exists!");
        }

        return savedUser;
    }

    public Users findByUsername(String username){
       return userRepository.findByUsername(username).orElseThrow(
               () -> new UsernameNotFoundException("Username not Found")
       );
    }

}
