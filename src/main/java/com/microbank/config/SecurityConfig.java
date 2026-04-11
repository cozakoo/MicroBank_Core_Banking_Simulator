package com.microbank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Dashboard y archivos estáticos sin autenticación (Desarrollo)
                        .requestMatchers("/", "/index.html", "/static/**", "/css/**", "/js/**").permitAll()

                        // Swagger sin autenticación
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/v1/health").permitAll()

                        // Admin requiere ADMIN
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // APIs sin autenticación (Desarrollo - comentar en Producción)
                        .requestMatchers("/api/v1/accounts/**", "/api/v1/transfers/**", "/api/v1/deposits/**", "/api/v1/withdrawals/**").permitAll()

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}