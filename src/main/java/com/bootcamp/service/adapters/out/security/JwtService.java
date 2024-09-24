package com.bootcamp.service.adapters.out.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.crypto.SecretKey;
import java.lang.reflect.Type;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;


@Service
public class JwtService {

    @Value("${jwt.expirationMs}")
    private long expirationTimeMs;

    private static final String SECRET_KEY = "mysecretkeymysecretkeymysecretkeymysecretkey"; // Clave secreta

    // Método para obtener la clave secreta como un objeto Key
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extraer el nombre de usuario del token de manera reactiva
    public Mono<String> extractUsername(String token) {
        return Mono.defer(() -> Mono.just(extractClaim(token, Claims::getSubject)).block());
    }

    // Extraer un reclamo específico del token de manera reactiva
    public <T> Mono<T> extractClaim(String token, Function<Claims, T> claimsResolver) {
        return Mono.fromCallable(() -> {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        });
    }

    // Extraer todos los reclamos del token de manera reactiva
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Validar el token JWT de manera reactiva
    public Mono<Boolean> validateToken(String token) {
        if (token == null) {
            return Mono.just(false);
        }
        return extractUsername(token)
                .flatMap(username -> isTokenExpired(token)
                        .map(isExpired -> !isExpired && username != null)
                )
                .onErrorResume(e -> {
                    // Loguea el error si es necesario
                    return Mono.just(false);
                });
    }




    public Mono<Boolean> isTokenExpired(String token) {
        return extractExpiration(token)
                .map(expirationDate -> expirationDate.before(new Date()));
    }



    // Extraer la fecha de expiración del token de manera reactiva
    public Mono<Date> extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generar un token basado en los detalles del usuario de manera reactiva
    public Mono<String> generateToken(UserDetails userDetails) {
        return Mono.fromCallable(() -> {
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList());
            return createToken(claims, userDetails.getUsername());
        });
    }

    // Crear el token JWT
    protected String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extraer los roles del token JWT de manera reactiva
    public Mono<List<String>> extractRoles(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = extractAllClaims(token);
            Object roles = claims.get("roles");

            if (roles != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<String>>() {}.getType();
                return gson.fromJson(roles.toString(), listType);
            }

            return new ArrayList<String>();
        });
    }

    // Obtener la autenticación desde el token JWT de manera reactiva
    public Mono<Authentication> getAuthentication(String token) {
        return extractUsername(token).flatMap(username -> {
            return extractRoles(token).map(roles -> {
                List<GrantedAuthority> authorities = roles.stream()
                        .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                        .collect(Collectors.toList());

                return new UsernamePasswordAuthenticationToken(username, null, authorities);
            });
        });
    }

    // Método para extraer el token del encabezado de la solicitud de manera reactiva
    public Mono<String> extractToken(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
            HttpHeaders headers = exchange.getRequest().getHeaders();
            String bearerToken = headers.getFirst(HttpHeaders.AUTHORIZATION);

            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
            return null;
        });
    }
}
