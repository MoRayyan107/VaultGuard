package com.guard.vaultguard.security.rateLimiting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guard.vaultguard.service.rateLimiting.IpRateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class IpRateLimitingFilter extends OncePerRequestFilter {

    private final IpRateLimitingService ipRateLimitingService;

    public IpRateLimitingFilter(IpRateLimitingService ipRateLimitingService) {
        this.ipRateLimitingService = ipRateLimitingService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // extract clients IP
        String clientIp = getClientIp(request);

        // get the token bucket
        Bucket tokenBucket = ipRateLimitingService.resolveBucket(clientIp);

        var probe = tokenBucket.tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed()){
            long waitRefill = probe.getNanosToWaitForRefill();
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");

            Map<String, Object> errorResponse = Map.of(
                    "error", "Rate limit exceeded",
                    "message", "You have exceeded the allowed number of requests. Please try again later.",
                    "retryAfterNanos", waitRefill
            );
            new ObjectMapper().writeValue(response.getWriter(), errorResponse);

            return;
        }

        response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
        filterChain.doFilter(request, response);

    }

    private String getClientIp(HttpServletRequest request) {
        // check for X-Forwarded-For header first (in case of proxies/load balancers)
        String xfHeader = request.getHeader("X-Forwarded-For");

        // if doesnt exists use the remote address
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }

        return xfHeader.split(",")[0].trim(); // In case of multiple IPs, take the first one
    }
}
