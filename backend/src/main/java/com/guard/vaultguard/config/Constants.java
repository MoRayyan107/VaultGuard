package com.guard.vaultguard.config;

import org.springframework.beans.factory.annotation.Value;

public final class Constants {

    // Controllers
    public static final String[] PUBLIC_ENDPOINTS = {
            // only login and logout are public, all other endpoints require authentication
            "/api/v1/auth/login",
            "/api/v1/auth/logout",
            "/error",  //  the error endpoint that gets thrown by Spring internal exceptions

            // Swagger
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    public static String AUTH_ENDPOINT_FOR_RATE_LIMITING_SKIP = "/api/v1/auth/logout";

    // Service
    public final static Integer MAX_TIME_DIFF_LOCATION_CHANGE_SECONDS = 300; // 5 minutes
    public final static Double RISKSCORE_THRESHOLD = 0.7;

    // Role Enum (compile-time string constants so they can be used in annotations)
    public final static String ROLE_MANAGER = "MANAGER";
    public final static String ROLE_ANALYST = "ANALYST";

    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}
