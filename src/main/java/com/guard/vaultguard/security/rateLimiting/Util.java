package com.guard.vaultguard.security.rateLimiting;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Util {

    private final ObjectMapper objectMapper;

    public Util(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        log.info("[INFO] Util class initialized");
    }

    public Long doConsume(HttpServletRequest request, HttpServletResponse response, Bucket bucket) throws IOException {
        log.info("[INFO] Consuming request: {}", request.getRequestURI());
        var probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            long waitRefillInSeconds = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");

                Map<String, Object> errorResponse = Map.of(
                        "error", "Rate limit exceeded",
                        "message", "You have exceeded the allowed number of requests. Please try again later.",
                        "retryAfterSeconds", waitRefillInSeconds+"s"
                );
                objectMapper.writeValue(response.getWriter(), errorResponse);

                return null;
        }

        // set the header for remaining tokens manually in filters
        return probe.getRemainingTokens();
    }
}
