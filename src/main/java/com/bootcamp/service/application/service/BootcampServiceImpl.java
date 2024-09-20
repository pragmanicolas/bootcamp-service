package com.bootcamp.service.application.service;

import com.bootcamp.service.application.port.in.BootcampService;
import com.bootcamp.service.application.port.out.BootcampRepository;
import com.bootcamp.service.domain.Bootcamp;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BootcampServiceImpl implements BootcampService {

    private final BootcampRepository bootcampRepository;

    public BootcampServiceImpl(BootcampRepository bootcampRepository) {
        this.bootcampRepository = bootcampRepository;
    }

    @Override
    public Mono<Bootcamp> createBootcamp(Bootcamp bootcamp) {
        return bootcampRepository.save(bootcamp);
    }

    @Override
    public Flux<Bootcamp> getAllBootcamps() {
        return bootcampRepository.findAll();
    }

    @Override
    public Mono<Bootcamp> getBootcampById(Long id) {
        return bootcampRepository.findById(id);
    }

    @Override
    public Mono<Bootcamp> updateBootcamp(Long id, Bootcamp bootcamp) {
        return bootcampRepository.findById(id)
                .flatMap(existingBootcamp -> {
                    existingBootcamp.setName(bootcamp.getName());
                    existingBootcamp.setDescription(bootcamp.getDescription());
                    return bootcampRepository.save(existingBootcamp);
                });
    }

    @Override
    public Mono<Void> deleteBootcamp(Long id) {
        return bootcampRepository.deleteById(id);
    }
}
