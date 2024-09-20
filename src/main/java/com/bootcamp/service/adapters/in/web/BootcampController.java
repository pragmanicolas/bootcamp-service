package com.bootcamp.service.adapters.in.web;


import com.bootcamp.service.application.port.in.BootcampService;
import com.bootcamp.service.domain.Bootcamp;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/bootcamps")
public class BootcampController {

    private final BootcampService bootcampService;

    public BootcampController(BootcampService bootcampService) {
        this.bootcampService = bootcampService;
    }

    @PostMapping
    public Mono<Bootcamp> createBootcamp(@RequestBody Bootcamp bootcamp) {
        return bootcampService.createBootcamp(bootcamp);
    }

    @GetMapping
    public Flux<Bootcamp> getAllBootcamps() {
        return bootcampService.getAllBootcamps();
    }

    @GetMapping("/{id}")
    public Mono<Bootcamp> getBootcampById(@PathVariable Long id) {
        return bootcampService.getBootcampById(id);
    }

    @PutMapping("/{id}")
    public Mono<Bootcamp> updateBootcamp(@PathVariable Long id, @RequestBody Bootcamp bootcamp) {
        return bootcampService.updateBootcamp(id, bootcamp);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteBootcamp(@PathVariable Long id) {
        return bootcampService.deleteBootcamp(id);
    }
}