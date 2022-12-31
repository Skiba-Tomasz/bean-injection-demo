package com.skibyte.demo;

import com.skibyte.demo.dto.ComplexMapField;
import com.skibyte.demo.dto.Example2Dto;
import com.skibyte.demo.dto.ExampleDto;
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
    private final ItemProvider<Example2Dto> itemProvider2;

    public Map<String, ComplexMapField<?>> provide(){
        ExampleDto dto = new ExampleDto(1, "test", LocalDateTime.now(), true);
        return itemProvider.convert(dto);
    }
    public Map<String, ComplexMapField<?>> provide2(){
        Example2Dto dto = new Example2Dto(9L, 2.0D);
        return itemProvider2.convert(dto);
    }

    @Override
    public void run(String... args) {
        log.info("provide = {}", this.provide());
        log.info("provide2 = {}", this.provide2());
        log.info("Finished");
    }
}
