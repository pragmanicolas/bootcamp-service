package com.bootcamp.service.application.service;

import com.bootcamp.service.application.port.out.BootcampRepository;
import com.bootcamp.service.domain.Bootcamp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootcampServiceImplTest {

    @Mock
    private BootcampRepository bootcampRepository;

    @InjectMocks
    private BootcampServiceImpl bootcampService;

    @Test
    void testCreateBootcamp() {
        Bootcamp bootcamp = new Bootcamp(null, "Java Bootcamp", "Learn Java");
        when(bootcampRepository.save(any())).thenReturn(Mono.just(bootcamp));

        Mono<Bootcamp> result = bootcampService.createBootcamp(bootcamp);

        StepVerifier.create(result)
                .expectNext(bootcamp)
                .verifyComplete();

        verify(bootcampRepository, times(1)).save(bootcamp);
    }
}
