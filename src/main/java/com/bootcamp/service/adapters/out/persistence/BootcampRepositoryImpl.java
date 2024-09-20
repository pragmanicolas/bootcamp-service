package com.bootcamp.service.adapters.out.persistence;

import com.bootcamp.service.application.port.out.BootcampRepository;
import com.bootcamp.service.domain.Bootcamp;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BootcampRepositoryImpl implements BootcampRepository {

    private final SpringDataBootcampRepository springDataBootcampRepository;

    public BootcampRepositoryImpl(SpringDataBootcampRepository springDataBootcampRepository) {
        this.springDataBootcampRepository = springDataBootcampRepository;
    }

    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return springDataBootcampRepository.save(bootcamp);
    }

    @Override
    public Flux<Bootcamp> findAll() {
        return springDataBootcampRepository.findAll();
    }

    @Override
    public Mono<Bootcamp> findById(Long id) {
        return springDataBootcampRepository.findById(id);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return springDataBootcampRepository.deleteById(id);
    }
}
