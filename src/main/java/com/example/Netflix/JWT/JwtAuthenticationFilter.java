package com.example.Netflix.JWT;

import com.example.Netflix.Security.CustomDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtTokenFactory jwtTokenFactory;
    private final CustomDetailsService customDetailsService;

    public JwtAuthenticationFilter(
            JwtTokenFactory jwtTokenFactory,
            HandlerExceptionResolver handlerExceptionResolver,
            CustomDetailsService customDetailsService
    ) {
        this.jwtTokenFactory = jwtTokenFactory;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.customDetailsService = customDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException
    {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            System.out.println("JWT: " + jwt);
            final String userEmail = jwtTokenFactory.getUsernameFromToken(jwt);
            System.out.println("User: " + userEmail);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            System.out.println("AUTH: " + authentication);

            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.customDetailsService.loadUserByUsername(userEmail);

                System.out.println("User details: " + userDetails);

                if (jwtTokenFactory.validateToken(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    System.out.println("Auth token: " + authToken);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            System.out.println("Security context: " + SecurityContextHolder.getContext());
            System.out.println("Request: " + request + "Response: " + response);

            try {
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }
    }
}
