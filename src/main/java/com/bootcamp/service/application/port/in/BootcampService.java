package com.bootcamp.service.application.port.in;

import com.bootcamp.service.domain.Bootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampService {
    Mono<Bootcamp> createBootcamp(Bootcamp bootcamp);
    Flux<Bootcamp> getAllBootcamps();
    Mono<Bootcamp> getBootcampById(Long id);
    Mono<Bootcamp> updateBootcamp(Long id, Bootcamp bootcamp);
    Mono<Void> deleteBootcamp(Long id);
}
