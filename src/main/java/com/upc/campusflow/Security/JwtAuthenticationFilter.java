package com.upc.campusflow.Security;

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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Log detallado para debugging
        System.out.println("🔍 === JWT FILTER DEBUG ===");
        System.out.println("🌐 URI: " + request.getMethod() + " " + requestURI);
        System.out.println("🔑 Authorization Header: " + (authHeader != null ? "Presente" : "❌ AUSENTE"));

        if (authHeader != null) {
            System.out.println("📋 Header completo: " + authHeader.substring(0, Math.min(50, authHeader.length())) + "...");
        }

        // Extraer token del header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println("🎫 Token extraído: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");

            try {
                username = jwtTokenUtil.getUsernameFromToken(jwt);
                System.out.println("👤 Username del token: " + username);
            } catch (Exception e) {
                System.err.println("❌ Error extrayendo username: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ No hay Bearer token en el header");
        }

        // Procesar autenticación si tenemos username y no hay auth previa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("🔄 Procesando autenticación para: " + username);

            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("👥 Authorities cargadas: " + userDetails.getAuthorities());

                // Validar token
                if (jwtTokenUtil.validateToken(jwt, userDetails.getUsername())) {
                    System.out.println("✅ Token válido para: " + username);

                    // Crear autenticación
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Establecer en SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println("🔐 Autenticación establecida exitosamente");
                    System.out.println("🎯 Authorities finales: " + authenticationToken.getAuthorities());
                } else {
                    System.err.println("❌ Token inválido para: " + username);
                }
            } catch (Exception e) {
                System.err.println("❌ Error cargando UserDetails para " + username + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else if (username == null) {
            System.out.println("⚠️ No hay username para procesar");
        } else {
            System.out.println("ℹ️ Ya existe autenticación previa");
        }

        // Verificar estado final de autenticación
        final var finalAuth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🏁 Estado final - Auth: " + (finalAuth != null ?
                "PRESENTE (" + finalAuth.getName() + ")" : "❌ NULL"));

        if (finalAuth != null) {
            System.out.println("🔓 Authorities finales: " + finalAuth.getAuthorities());
        }

        System.out.println("🔍 === FIN JWT FILTER DEBUG ===\n");

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}