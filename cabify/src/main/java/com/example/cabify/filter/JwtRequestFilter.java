package com.example.cabify.filter;

import com.example.cabify.service.CustomUserDetailsService;
import com.example.cabify.service.CustomDriverDetailsService;
import com.example.cabify.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomDriverDetailsService driverDetailsService; // 🚀 Injecting your new Driver service

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // CHANGE 1: Early return for CORS pre-flight requests to bypass security checks
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI(); // 🚀 Capture the URL path

        String username = null;
        String jwt = null;

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                // CHANGE 2: Add .trim() to strip whitespace
                jwt = authorizationHeader.substring(7).trim();

                // CHANGE 3: Structural validation to prevent parser exceptions
                if (jwt.equals("null") || jwt.equals("undefined") || jwt.split("\\.").length != 3) {
                    logger.warn("Rejected malformed JWT string from client: " + jwt);
                    chain.doFilter(request, response);
                    return; // Halt JWT processing and pass to the next filter
                }

                username = jwtUtil.extractUsername(jwt);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails;

                // 🚀 SMART ROUTING: Check which database table to search based on the URL
                if (requestURI.contains("/api/drivers") ||
                        requestURI.contains("/active-request") ||
                        requestURI.contains("/driver/history") ||
                        requestURI.contains("/end")) {

                    // Look in the Drivers table
                    userDetails = this.driverDetailsService.loadUserByUsername(username);
                } else {
                    // Default to Users (Riders) table
                    userDetails = this.userDetailsService.loadUserByUsername(username);
                }

                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // 🚀 Catching general exceptions (like UserNotFound) so it doesn't crash the whole app
            logger.warn("Authentication failed: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }
}