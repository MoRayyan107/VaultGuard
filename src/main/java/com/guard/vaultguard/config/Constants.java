package com.guard.vaultguard.config;

import com.guard.vaultguard.entities.enums.UserRole;

public final class Constants {

    // Controllers
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**",     // authentication endpoints (login logout)

            // Swagger
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    // Service
    public final static Integer MIN_TIME_DIFF_LOCATION_CHANGE_SECONDS = 120;
    public final static Integer MAX_TIME_DIFF_LOCATION_CHANGE_SECONDS = 300;
    public final static Double RISKSCORE_THRESHOLD = 0.7;

    // Role Enum (compile-time string constants so they can be used in annotations)
    public final static String ROLE_USER = "USER";
    public final static String ROLE_MANAGER = "MANAGER";
    public final static String ROLE_ANALYST = "ANALYST";


    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}
