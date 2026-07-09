package com.example.chapter13.security;

import com.example.chapter13.config.AiSecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AiSecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/ai-testing/**").permitAll()
                        .requestMatchers("/evaluations/**", "/metrics/ai").hasRole("EVALUATOR")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(AiSecurityProperties securityProperties) {
        UserDetails evaluator = User.withUsername(securityProperties.evaluatorUsername())
                .password("{noop}" + securityProperties.evaluatorPassword())
                .roles(securityProperties.evaluatorRole())
                .build();
        UserDetails employee = User.withUsername("EMP-1001")
            .password("{noop}employee-pass")
            .roles("EMPLOYEE")
            .build();
        UserDetails hr = User.withUsername("hr-user")
            .password("{noop}hr-pass")
            .roles("HR")
            .build();
        UserDetails finance = User.withUsername("finance-user")
            .password("{noop}finance-pass")
            .roles("FINANCE")
            .build();
        UserDetails operations = User.withUsername("ops-user")
            .password("{noop}ops-pass")
            .roles("OPERATIONS")
            .build();
        UserDetails admin = User.withUsername("admin-user")
            .password("{noop}admin-pass")
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(evaluator, employee, hr, finance, operations, admin);
    }
}
