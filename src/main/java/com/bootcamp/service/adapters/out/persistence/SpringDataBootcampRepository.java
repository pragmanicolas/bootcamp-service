package com.bootcamp.service.adapters.out.persistence;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SpringDataBootcampRepository extends ReactiveCrudRepository<BootcampEntity, Long> {
}
