package me.zedaster.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.zedaster.authservice.exception.JwtException;
import me.zedaster.authservice.security.auth.UsernameOrEmailAuthentication;
import me.zedaster.authservice.service.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";

    /**
     * Utils for working with JWT token.
     */
    private final JwtService jwtService;

    /**
     * Service for loading user details (for Spring Security) by ID.
     */
    private final IdUserDetailsService idUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HEADER_NAME);
        // If there is no auth token or auth context, then skip the filter
        Authentication contextAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX) || contextAuthentication != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = extractAccessToken(authHeader);

        long userId;
        try {
            userId = jwtService.verifyAndExtractUserId(accessToken);
        } catch (JwtException e) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = idUserDetailsService.loadUserById(userId);
        UsernameOrEmailAuthentication authentication = UsernameOrEmailAuthentication.createAuthenticated(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String extractAccessToken(String authHeader) {
        return authHeader.substring(BEARER_PREFIX.length());
    }
}
