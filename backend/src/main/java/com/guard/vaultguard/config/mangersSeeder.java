package com.guard.vaultguard.config;

import com.guard.vaultguard.entities.Users;
import com.guard.vaultguard.entities.enums.UserRole;
import com.guard.vaultguard.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;


@Profile("managers-seeder")
@Configuration
@AllArgsConstructor
public class mangersSeeder {

    private final UserRepository userRepository;

    @Bean
    CommandLineRunner seedManagers(PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if managers already exist
            if (userRepository.count() == 0) {

                Users manager1 = Users.builder()
                        .role(UserRole.MANAGER)
                        .username("manager1")
                        .password(passwordEncoder.encode("password1"))
                        .email("manager1@xyz.om")
                        .build();

                // Create and save manager users
                userRepository.save(manager1);
                System.out.println("Manager users seeded.");
            } else {
                System.out.println("Manager users already exist. Skipping seeding.");
            }
        };
    }

}
