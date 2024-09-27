package com.bootcamp.service.application.service;

import com.bootcamp.service.application.port.in.BootcampService;
import com.bootcamp.service.application.port.out.BootcampRepository;
import com.bootcamp.service.domain.Bootcamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BootcampServiceImpl implements BootcampService {

    private static final Logger logger = LoggerFactory.getLogger(BootcampServiceImpl.class);

    private final BootcampRepository bootcampRepository;

    public BootcampServiceImpl(BootcampRepository bootcampRepository) {
        this.bootcampRepository = bootcampRepository;
    }

    @Override
    public Mono<Bootcamp> createBootcamp(Bootcamp bootcamp) {
        logger.info("Creando nuevo bootcamp: {}", bootcamp);
        return bootcampRepository.save(bootcamp)
                .doOnSuccess(savedBootcamp -> logger.info("Bootcamp creado exitosamente: {}", savedBootcamp))
                .doOnError(e -> logger.error("Error al crear bootcamp", e))
                .onErrorResume(e -> Mono.error(new RuntimeException("Error creando el bootcamp, por favor revisa los datos y vuelve a intentar.")));
    }

    @Override
    public Flux<Bootcamp> getAllBootcamps() {
        logger.info("Obteniendo todos los bootcamps");
        return bootcampRepository.findAll()
                .doOnError(e -> logger.error("Error al obtener bootcamps", e));
    }

    @Override
    public Mono<Bootcamp> getBootcampById(Long id) {
        logger.info("Obteniendo bootcamp por ID: {}", id);
        return bootcampRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Bootcamp no encontrado con el ID: " + id)))
                .doOnError(e -> logger.error("Error al obtener bootcamp por ID: {}", id, e));
    }

    @Override
    public Mono<Bootcamp> updateBootcamp(Long id, Bootcamp bootcamp) {
        logger.info("Actualizando bootcamp con ID: {}", id);
        return bootcampRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Bootcamp no encontrado para actualización con ID: " + id)))
                .flatMap(existingBootcamp -> {
                    existingBootcamp.setName(bootcamp.getName());
                    existingBootcamp.setDescription(bootcamp.getDescription());
                    return bootcampRepository.save(existingBootcamp);
                })
                .doOnSuccess(updatedBootcamp -> logger.info("Bootcamp actualizado exitosamente: {}", updatedBootcamp))
                .doOnError(e -> logger.error("Error al actualizar bootcamp", e));
    }

    @Override
    public Mono<Void> deleteBootcamp(Long id) {
        logger.info("Eliminando bootcamp con ID: {}", id);
        return bootcampRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Bootcamp no encontrado para eliminación con ID: " + id)))
                .flatMap(existingBootcamp -> bootcampRepository.deleteById(id))
                .doOnSuccess(v -> logger.info("Bootcamp eliminado con éxito"))
                .doOnError(e -> logger.error("Error al eliminar bootcamp", e));
    }
}

