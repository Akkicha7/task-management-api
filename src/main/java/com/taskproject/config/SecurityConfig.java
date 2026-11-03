package com.taskproject.config;

import com.taskproject.security.JwtAuthenticationEntryPoint;
import com.taskproject.security.JwtAccessDeniedHandler;
import com.taskproject.security.JwtAuthenticationFilter;
import com.taskproject.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Central Spring Security configuration.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ── CSRF ────────────────────────────────────────────────────────
            // This is a stateless REST API authenticated via JWT Bearer tokens,
            // not browser session cookies, so CSRF protection is not applicable.
            .csrf(csrf -> csrf.disable())
            // ── Frame options ────────────────────────────────────────────────
            // Allow the H2 console (which uses frames) to load from the same origin.
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            // ── Session ──────────────────────────────────────────────────────
            .sessionManagement(sess -> sess
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // ── Exception handling ───────────────────────────────────────────
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            // ── Authorization ────────────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",       // public auth endpoints
                    "/swagger-ui/**",     // Swagger UI static files
                    "/swagger-ui.html",
                    "/v3/api-docs/**",    // OpenAPI spec
                    "/h2-console/**"      // H2 web console (dev only)
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
