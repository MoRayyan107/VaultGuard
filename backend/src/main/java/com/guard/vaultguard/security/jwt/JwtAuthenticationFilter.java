package com.guard.vaultguard.security.jwt;

import com.guard.vaultguard.security.userSecurity.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailServiceImpl userDetailService;

    @Value("${app.cookie.name}")
    private String cookieName;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailServiceImpl userDetailService) {
        this.jwtUtil = jwtUtil;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // old way -> check the token in header Authorization
        // new way -> get the jwt from cookie
        String username = null;
        String token = null;
        Cookie[] cookies = request.getCookies();

        // if cookie is missing, continue the filter chain without authentication (error will be handled by the exception handler)
        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // if cookie exists get the cookie header named jwt
        for (Cookie cookie: cookies){
            if (cookie.getName().equals(cookieName)){
                token = cookie.getValue();
                break;
            }
        }

        // if the username is not null and user is not authenticated in context
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                username = jwtUtil.extractUsername(token); // extract the username from the token

                if (username != null) {
                    UserDetails userDetail = userDetailService.loadUserByUsername(username); // fetch the user details from the application context

                    if (jwtUtil.validateToken(token, userDetail)) { // validate the token with the username

                        // if valid set the authentication in the security context
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetail, null, userDetail.getAuthorities()
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                // log the error and continue the filter chain without authentication
                log.error("Error during JWT authentication: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response); // ccontinue the filter chain
    }
}
