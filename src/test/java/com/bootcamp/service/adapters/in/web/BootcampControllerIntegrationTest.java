package com.bootcamp.service.adapters.in.web;

import com.bootcamp.service.adapters.out.security.JwtService;
import com.bootcamp.service.application.port.in.BootcampService;
import com.bootcamp.service.application.service.TestSecurityConfig;
import com.bootcamp.service.domain.Bootcamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class BootcampControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BootcampService bootcampService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private ReactiveUserDetailsService reactiveUserDetailsService;

    @Autowired
    private JwtService jwtService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        UserDetails userDetails = new User("user", "password", Collections.emptyList());

        // Mockeamos la autenticación del usuario
        when(reactiveUserDetailsService.findByUsername(anyString()))
                .thenReturn(Mono.just(userDetails));

        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(userDetails);

        // Generamos un token JWT válido usando UserDetails
        jwtToken = "Bearer " + jwtService.generateToken(userDetails).block();

        System.out.println("Generated JWT Token: " + jwtToken);  // Imprimir el token para depurar
    }



    @Test
    void testCreateBootcamp() {
        Bootcamp bootcamp = new Bootcamp(1L, "Spring Boot", "Description");
        when(bootcampService.createBootcamp(any(Bootcamp.class))).thenReturn(Mono.just(bootcamp));

        webTestClient
                .post()
                .uri("/api/bootcamps")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)  // Usamos el token JWT generado
                .bodyValue(bootcamp)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Bootcamp.class)
                .value(response -> assertEquals("Spring Boot", response.getName()));
    }

    @Test
    void testGetBootcampById() {
        Bootcamp bootcamp = new Bootcamp(1L, "Spring Boot", "Description");
        when(bootcampService.getBootcampById(1L)).thenReturn(Mono.just(bootcamp));

        webTestClient.get()
                .uri("/api/bootcamps/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)  // Usamos el token JWT generado
                .exchange()
                .expectStatus().isOk()
                .expectBody(Bootcamp.class)
                .value(response -> assertEquals("Spring Boot", response.getName()));
    }

}

