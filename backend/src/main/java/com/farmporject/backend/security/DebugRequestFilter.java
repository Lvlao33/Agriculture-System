package com.farmporject.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class DebugRequestFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_ATTR = "X-Request-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String id = UUID.randomUUID().toString();
        request.setAttribute(REQUEST_ID_ATTR, id);
        try {
            System.out.println("[REQ-START] id=" + id + " method=" + request.getMethod() + " uri=" + request.getRequestURI());
            filterChain.doFilter(request, response);
        } finally {
            System.out.println("[REQ-END] id=" + id + " status=" + response.getStatus());
        }
    }
}
