package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderService implements CommandLineRunner {

    private final ItemProvider<ExampleDto> itemProvider;

    public Map<String, ComplexMapField<?>> provide(){
        ExampleDto dto = new ExampleDto(1, "test", LocalDateTime.now(), true);
        return itemProvider.convert(dto);
    }

    @Override
    public void run(String... args) {
        Map<String, ComplexMapField<?>> provide = this.provide();

        log.info("provide = {}", provide);
        log.info("Finished");
    }
}
