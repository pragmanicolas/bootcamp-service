package com.bootcamp.service.adapters.out.persistence;

import com.bootcamp.service.domain.Bootcamp;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SpringDataBootcampRepository extends ReactiveCrudRepository<Bootcamp, Long> {
}
