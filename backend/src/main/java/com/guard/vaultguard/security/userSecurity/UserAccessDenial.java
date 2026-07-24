package com.guard.vaultguard.security.userSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class UserAccessDenial implements AccessDeniedHandler {
    private final ObjectMapper mapper;

    public UserAccessDenial(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Map<String, Object> responseBody = Map.of(
                "error", "Forbidden",
                "message", accessDeniedException.getMessage(),
                "path", request.getRequestURI()
        );
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(mapper.writeValueAsString(responseBody));
    }
}
