package com.bootcamp.service.application.port.out;

import com.bootcamp.service.domain.Bootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampRepository {
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Flux<Bootcamp> findAll();
    Mono<Bootcamp> findById(Long id);
    Mono<Void> deleteById(Long id);
}
