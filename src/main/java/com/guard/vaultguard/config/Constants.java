package com.guard.vaultguard.config;

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

    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}
