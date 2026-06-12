package com.guard.vaultguard.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // for now simple allow all requests
        // updating this later in future

        // First CORS custom check
        // Redis Rate limit check
        // JWT for authentication
        // Authorise which rol can acces endpoints
        // then let them hit the endpoint

        http.csrf(
                AbstractHttpConfigurer::disable
        ).authorizeHttpRequests(
                auth -> auth.anyRequest().permitAll()
        );

        return http.build();
    }
}
