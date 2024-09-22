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
        // Convertir de Bootcamp a BootcampEntity antes de guardar
        BootcampEntity entity = new BootcampEntity(bootcamp.getId(), bootcamp.getName(), bootcamp.getDescription());
        return springDataBootcampRepository.save(entity)
                // Convertir de BootcampEntity a Bootcamp después de guardar
                .map(savedEntity -> new Bootcamp(savedEntity.getId(), savedEntity.getName(), savedEntity.getDescription()));
    }

    @Override
    public Flux<Bootcamp> findAll() {
        // Convertir de BootcampEntity a Bootcamp al recuperar los datos
        return springDataBootcampRepository.findAll()
                .map(entity -> new Bootcamp(entity.getId(), entity.getName(), entity.getDescription()));
    }

    @Override
    public Mono<Bootcamp> findById(Long id) {
        // Convertir de BootcampEntity a Bootcamp al buscar por ID
        return springDataBootcampRepository.findById(id)
                .map(entity -> new Bootcamp(entity.getId(), entity.getName(), entity.getDescription()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return springDataBootcampRepository.deleteById(id);
    }
}
