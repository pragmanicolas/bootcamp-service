package com.bootcamp.service.adapters.in.security;

import com.bootcamp.service.adapters.out.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Component
public class JwtRequestFilter implements WebFilter {

    private final JwtService jwtService;
    private final ReactiveUserDetailsService reactiveUserDetailsService;

    public JwtRequestFilter(JwtService jwtService, ReactiveUserDetailsService reactiveUserDetailsService) {
        this.jwtService = jwtService;
        this.reactiveUserDetailsService = reactiveUserDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return extractToken(exchange)
                .flatMap(token -> jwtService.validateToken(token)
                        .flatMap(isValid -> {
                            if (isValid) {
                                // Si el token es válido, continuar con el siguiente filtro
                                return chain.filter(exchange);
                            } else {
                                // Si no es válido, devolver 401
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                return exchange.getResponse().setComplete();
                            }
                        })
                )
                .switchIfEmpty(chain.filter(exchange))  // Si no hay token, continúa la cadena de filtros
                .onErrorResume(e -> {
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
    }

    private Mono<String> extractToken(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .map(authHeader -> authHeader.substring(7));
    }
}

