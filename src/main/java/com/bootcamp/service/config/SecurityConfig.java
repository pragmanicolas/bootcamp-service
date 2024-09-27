package com.bootcamp.service.config;


import com.bootcamp.service.adapters.in.security.CustomUserDetailsService;
import com.bootcamp.service.adapters.in.security.JwtRequestFilter;
import io.netty.handler.codec.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/auth/login").permitAll()
                        .pathMatchers("/api/auth/**").permitAll()
                        // Permitir solo al rol "ADMIN" acceder a POST en /api/bootcamps
                        .pathMatchers(String.valueOf(HttpMethod.POST), "/api/bootcamps").hasRole("ADMIN")
                        .pathMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyExchange().authenticated())
                .addFilterAt(jwtRequestFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // Inyectamos directamente el ReactiveUserDetailsService
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService reactiveUserDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
    }

}
