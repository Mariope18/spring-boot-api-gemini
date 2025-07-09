package com.esempio.primoprogetto.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Indica a Spring di gestire questa classe come un componente
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Controlla se l'header Authorization esiste e se inizia con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Se non c'è, passa al prossimo filtro e termina
            return;
        }

        // 2. Estrai il token dall'header
        jwt = authHeader.substring(7); // "Bearer " sono 7 caratteri

        // 3. Estrai lo username dal token usando il JwtService
        username = jwtService.extractUsername(jwt);

        // 4. Controlla se lo username esiste E se l'utente non è già autenticato
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Carica i dettagli dell'utente dal database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            // 5. Controlla se il token è valido
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 6. Se il token è valido, aggiorna il SecurityContextHolder per autenticare l'utente
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Non servono le credenziali (password) perché stiamo usando il token
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Aggiorna il contesto di sicurezza
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 7. Passa al prossimo filtro della catena
        filterChain.doFilter(request, response);
    }
}