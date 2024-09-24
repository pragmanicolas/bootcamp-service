package com.bootcamp.service.adapters.in.web;

import com.bootcamp.service.adapters.out.security.JwtService;
import com.bootcamp.service.application.port.in.BootcampService;
import com.bootcamp.service.domain.Bootcamp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
class BootcampControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BootcampService bootcampService;

    @MockBean
    private JwtService jwtService; // Añadir esto

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private ReactiveUserDetailsService reactiveUserDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void testCreateBootcamp() {
        Bootcamp bootcamp = new Bootcamp(null, "Java Bootcamp", "Learn Java");

        // Simulación de comportamiento del servicio
        when(bootcampService.createBootcamp(any())).thenReturn(Mono.just(bootcamp));

        // Crear la autenticación simulada
        UsernamePasswordAuthenticationToken mockAuth =
                new UsernamePasswordAuthenticationToken(
                        "user", // username
                        "password", // password
                        List.of(new SimpleGrantedAuthority("ROLE_USER")) // roles o autoridades
                );

        // Simular el comportamiento de seguridad con WebTestClient
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockAuthentication(mockAuth))
                .post().uri("/api/bootcamps")
                .bodyValue(bootcamp)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Java Bootcamp");
    }
}
