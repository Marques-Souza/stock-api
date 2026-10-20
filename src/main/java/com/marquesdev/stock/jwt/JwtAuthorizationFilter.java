package com.marquesdev.stock.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUserDetailsService detailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = request.getHeader((JwtUtils.JWT_AUTHORIZATION));
        if (token == null || !token.startsWith(JwtUtils.JWT_BEARER)){
            log.info("JWT token is missing or invalid.");
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtUtils.isTokenValid(token)){
            log.warn("JWT is invalid.");
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtUtils.getEmail(token);
        toAuthentication(request, email);
        filterChain.doFilter(request, response);
    }

    private void toAuthentication(HttpServletRequest request, String email){
        UserDetails userDatails = detailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDatails, null, userDatails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
