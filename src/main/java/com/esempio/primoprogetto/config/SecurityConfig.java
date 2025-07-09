package com.esempio.primoprogetto.config;

import com.esempio.primoprogetto.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabilita CSRF, non necessario per API stateless
                .authorizeHttpRequests(auth -> auth
                        // 1. Usa PathRequest.toH2Console() per permettere l'accesso alla console H2
                        // Questo è il modo moderno e raccomandato, gestisce tutti i path necessari.
                        .requestMatchers(PathRequest.toH2Console()).permitAll()

                        // 2. Permetti l'accesso agli endpoint di autenticazione
                        .requestMatchers("/api/v1/auth/**").permitAll() // Pubblico per tutti

                        // 3. Proteggi gli altri endpoint con i ruoli
                        .requestMatchers("/todos/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // 4. Qualsiasi altra richiesta deve essere autenticata
                        .anyRequest().authenticated()
                )
                // Configurazione della sessione (corretta per API stateless)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Imposta la gestione della sessione su STATELESS

                // Disabilitiamo la protezione dei frame per permettere alla console H2 di funzionare
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())) // <-- AGGIUNGI QUESTO BLOCCO

                // Impostiamo il provider di autenticazione e il nostro filtro JWT
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}