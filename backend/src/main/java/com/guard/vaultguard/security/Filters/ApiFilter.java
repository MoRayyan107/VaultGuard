package com.guard.vaultguard.security.Filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guard.vaultguard.entities.Bank;
import com.guard.vaultguard.security.principals.BankPrincipal;
import com.guard.vaultguard.security.util.ApiFilterUtil;
import com.guard.vaultguard.service.BankService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class ApiFilter extends OncePerRequestFilter {

    private static final String BANK_API_PATH_PREFIX = "/api/v1/proccess/";

    private final BankService bankService;
    private final ObjectMapper objectMapper;

    @Value("${bank.api.key.starter}")
    private String bankApiKeyStarter;


    public ApiFilter(BankService bankService, ObjectMapper objectMapper) {
        this.bankService = bankService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // This filter is only for bank API-key routes, not user auth endpoints.
        return !request.getRequestURI().startsWith(BANK_API_PATH_PREFIX);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("[INFO] ApiFilter: Filtering request for URI: {}", request.getRequestURI());
        // get from headers if thers an existing header named "X-API-KEY"
        String apiKey = request.getHeader("X-API-KEY");
        log.info("[INFO] ApiFilter: Retrieved API Key from header: {}", apiKey);

        // if the header is missing manually make a 403 respionse and return
        if (apiKey == null || apiKey.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                    "error", "Unauthorized",
                    "message", "Missing API Key",
                    "path", request.getRequestURI()
            )));
            log.info("[INFO] ApiFilter: Filtering request for URI: {}", request.getRequestURI()+ "Failed due to missing API Key");
            return;
        }

        // if the string exists then cchecck from the DB if this key matcches with the bank ornot
        if (SecurityContextHolder.getContext().getAuthentication() == null){
            try{
                // strip the prefix
                if (apiKey.startsWith(bankApiKeyStarter)){
                    apiKey = apiKey.substring(bankApiKeyStarter.length()); // strips the prefix from the api key
                }
                String hashedApiKey = ApiFilterUtil.hashApiKey(apiKey);

                log.info("[INFO] ApiFilter: Hashed API Key: {}", hashedApiKey);
                // fetcch from the DB and compare with the hashed api key
                Optional<Bank> bank = bankService.getBankByApiKey(hashedApiKey);

                if (bank.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                            "error", "Unauthorized",
                            "message", "Invalid API Key",
                            "path", request.getRequestURI()
                    )));
                    log.info("[INFO] ApiFilter: Filtering request for URI: {}", request.getRequestURI()+ "Failed due to invalid API Key");
                    return;
                }

                // if bank is present but not active then return 403
                if (!bank.get().isActive()) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                            "error", "Forbidden",
                            "message", "Bank is not active",
                            "path", request.getRequestURI()
                    )));
                    log.info("[INFO] ApiFilter: Filtering request for URI: {}", request.getRequestURI()+ "Failed due to bank not active");
                    return;
                }

                // set the princcipal
                BankPrincipal bankPrincipal = new BankPrincipal(bank.get());

                // since bank is not null and acctive set the Authentication in the security context
                Authentication authToken = new UsernamePasswordAuthenticationToken(
                        bankPrincipal, null, bankPrincipal.getAuthorities()
                );
                log.info("[INFO] ApiFilter: Setting authentication in security context for URI: {}", request.getRequestURI());
                // set the seccurity ccontext)
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("[INFO] ApiFilter: Setting authentication in security context");
            } catch (NoSuchAlgorithmException e) {
                // Handle the exception
                log.warn("[WARN] Error hashing API key: {}", e.getMessage());
            }
        }

        // finally once everything is secure then filter
        log.info("[INFO] ApiFilter: Filtering request for URI: {}", request.getRequestURI()+ "Passed");
        filterChain.doFilter(request, response);
    }

}
