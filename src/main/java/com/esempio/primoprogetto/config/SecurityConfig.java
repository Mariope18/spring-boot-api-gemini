package com.esempio.primoprogetto.config;

import com.esempio.primoprogetto.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
                // Disabilitiamo CSRF, che è standard per API stateless
                .csrf(csrf -> csrf.disable())

                // Definiamo le regole di autorizzazione per le richieste HTTP
                .authorizeHttpRequests(auth -> auth
                        // Elenco di tutti i percorsi che devono essere PUBBLICI
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/h2-console/**"
                        ).permitAll()

                        // Regole specifiche per i ruoli
                        .requestMatchers("/todos/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // Qualsiasi altra richiesta deve essere autenticata
                        .anyRequest().authenticated()
                )

                // Gestione della sessione: deve essere STATELESS perché usiamo JWT
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Disabilitiamo la protezione dei frame per permettere alla console H2 di funzionare
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable()))

                // Impostiamo il nostro provider di autenticazione
                .authenticationProvider(authenticationProvider)

                // Aggiungiamo il nostro filtro JWT prima del filtro standard di Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
