package com.guard.vaultguard.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guard.vaultguard.security.jwt.JwtAuthenticationEntryPoint;
import com.guard.vaultguard.security.jwt.JwtAuthenticationFilter;
import com.guard.vaultguard.security.rateLimiting.IpRateLimitingFilter;
import com.guard.vaultguard.security.rateLimiting.UserRateLimitingFilter;
import com.guard.vaultguard.security.userSecurity.UserAccessDenial;
import com.guard.vaultguard.security.userSecurity.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.guard.vaultguard.config.Constants.PUBLIC_ENDPOINTS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final ObjectMapper mapper;
    private final JwtAuthenticationFilter jwtFilter;
    private final UserDetailServiceImpl userDetailService;
    private final IpRateLimitingFilter ipRateLimitingFilter;
    private final UserRateLimitingFilter userRateLimitingFilter;

    public SecurityConfig(UserDetailServiceImpl userDetailService,
                          JwtAuthenticationFilter jwtFilter,
                          IpRateLimitingFilter ipRateLimitingFilter,
                          ObjectMapper mapper, UserRateLimitingFilter userRateLimitingFilter)
    {
        this.mapper = mapper;
        this.jwtFilter = jwtFilter;
        this.userDetailService = userDetailService;
        this.ipRateLimitingFilter = ipRateLimitingFilter;
        this.userRateLimitingFilter = userRateLimitingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName(null);

        return http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(requestHandler)
                        .ignoringRequestMatchers(PUBLIC_ENDPOINTS))
//                .csrf(csrf -> csrf.disable()) // for now disable csrf, will enable later once FE is implemented or any microservices are calling this API
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // for now disable cors, will enable later oncce FE is implemented or any microservices are calling this API
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(ipRateLimitingFilter, JwtAuthenticationFilter.class) // Add before we cchecck the token
                .addFilterAfter(userRateLimitingFilter, JwtAuthenticationFilter.class) // Add after we check the token, so we can get the user from the token and rate limit based on user
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtEntryPoint()))  // authentication entry point for 401's
                .exceptionHandling(ex -> ex.accessDeniedHandler(userAccessDenialHandler()))  // access denied handler for 403's
                .build();

    }

    // used for encoding password when registering
    // Delegating password encoder -> rather than using BCrypt as hardcoded method we use this
    //                             -> this creates hashed passwords with variety of Hashing Algorithms
    //                             -> Most cconvinent way to use, stores in {hashingAlgo name}key
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // set UserDetailService and PasswordEncoder for authentication provider
    @Bean
    public AuthenticationProvider AuthProvider () {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Authenticatio manager for authentication provider, used in AuthController for login
    @Bean
    public AuthenticationManager authManager (AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // had issues with 403 and 401 errors, this is the entry point for unauthenticated requests, returns 401 with json response
    @Bean
    public AuthenticationEntryPoint jwtEntryPoint() {
        return new JwtAuthenticationEntryPoint(mapper);
    }

    @Bean
    public AccessDeniedHandler userAccessDenialHandler() {
        return new UserAccessDenial(mapper);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // allows diffrent cross site applications to use APIs
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
