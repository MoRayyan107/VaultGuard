package com.guard.vaultguard.security.rateLimiting;

import com.guard.vaultguard.service.rateLimiting.IpRateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class IpRateLimitingFilter extends OncePerRequestFilter {

    private final IpRateLimitingService ipRateLimitingService;
    private final Util ipRateLimitingUtil;

    public IpRateLimitingFilter(IpRateLimitingService ipRateLimitingService, Util ipRateLimitingUtil) {
        this.ipRateLimitingService = ipRateLimitingService;
        this.ipRateLimitingUtil = ipRateLimitingUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // extract clients IP
        String clientIp = getClientIp(request);

        // get the token bucket
        Bucket IpTokenBucket = ipRateLimitingService.resolveBucket(clientIp);

        // consume the token form util class
        var remainingTokens_IP = ipRateLimitingUtil.doConsume(request, response, IpTokenBucket);

        // the error message is parsed so if no remaining tokens are left the request is rejected and the error message is sent to the client
        if (remainingTokens_IP == null){
            return;
        }

        response.addHeader("X-IP-Rate-Limit-Remaining", String.valueOf(remainingTokens_IP));
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
