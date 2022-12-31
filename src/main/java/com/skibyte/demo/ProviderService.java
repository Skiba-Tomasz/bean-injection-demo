package com.skibyte.demo;

import com.skibyte.demo.dto.*;
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

//    private final ItemProvider<ExampleDto> itemProvider;
//    private final ItemProvider<Example2Dto> itemProvider2;
    private final ItemProvider<Example3Dto> itemProvider3;

//    public Map<String, ComplexMapField<?>> provide(){
//        ExampleDto dto = new ExampleDto(1, "test", LocalDateTime.now(), true);
//        return itemProvider.convert(dto);
//    }
//    public Map<String, ComplexMapField<?>> provide2(){
//        Example2Dto dto = new Example2Dto(9L, 2.0D);
//        return itemProvider2.convert(dto);
//    }

    public Map<String, ComplexMapField<?>> provide3(){
        Example3Dto dto = new Example3Dto(9L, 2.0D, new NestedDto(9L, 2.0D, new Nested2Dto(123L, 321.0D)));
        return itemProvider3.convert(dto);
    }

    @Override
    public void run(String... args) {
        Map<String, ComplexMapField<?>> test = this.provide3();
//        log.info("provide = {}", this.provide());
//        log.info("provide2 = {}", this.provide2());
        log.info("provide3 = {}", test);
        log.info("Finished");
    }
}
