package com.guard.vaultguard.security.rateLimiting;

import com.guard.vaultguard.service.rateLimiting.UserRateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserRateLimitingFilter extends OncePerRequestFilter {

    private final UserRateLimitingService userRateLimitingService;
    private final Util rateLimiterUtil;

    public UserRateLimitingFilter(UserRateLimitingService userRateLimitingService, Util rateLimiterUtil){
        this.userRateLimitingService = userRateLimitingService;
        this.rateLimiterUtil = rateLimiterUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // get the security context and get the authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()){
            // get the username from the authentication
            String usernameOrId = authentication.getName();

            // fetch or allocate an isolated bucket for that user
            Bucket userBucket = userRateLimitingService.resolveUserBucket(usernameOrId);

            // consume a token from that bucket
            var remainingTokens = rateLimiterUtil.doConsume(request, response, userBucket);

            if(remainingTokens == null){
                return;
            }

            response.addHeader("X-User-Rate-Limit-Remaining", String.valueOf(remainingTokens));
        }
        filterChain.doFilter(request, response);
    }
}
