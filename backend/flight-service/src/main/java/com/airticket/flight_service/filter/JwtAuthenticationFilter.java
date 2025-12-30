package com.airticket.flight_service.filter;

import com.airticket.flight_service.dto.CustomerAuthResponseDTO;
import com.airticket.flight_service.feign.CustomerClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final CustomerClient customerClient;

    public JwtAuthenticationFilter(CustomerClient customerClient) {
        this.customerClient = customerClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        //  Skip JWT check for GET /api/flights/**
        if (method.equalsIgnoreCase("GET") && path.startsWith("/api/flights")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing or invalid token");
            return;
        }

        CustomerAuthResponseDTO customerAuthResponse = customerClient.validateToken(authHeader).getBody();

        logger.info("CustomerAuthResponseDTO received: {}", customerAuthResponse);
        if (!customerAuthResponse.isValid()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access Declined");
            return;
        }

        // Create authentication with role
        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + customerAuthResponse.getRole().toUpperCase()));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        customerAuthResponse.getCustomerId(), // principal (can be ID or username)
                        null, // credentials
                        authorities
                );

        // Store in context so Spring Security can use it
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
