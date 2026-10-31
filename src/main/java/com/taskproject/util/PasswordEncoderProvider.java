package com.taskproject.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Provides a {@link PasswordEncoder} bean for the application.
 * Using BCrypt with the default strength (10) is sufficient for most demos.
 */
@Configuration
public class PasswordEncoderProvider {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
