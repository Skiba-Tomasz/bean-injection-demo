package com.skibyte.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MapConverter
public class Example2Dto {
    private Long longValue;
    private Double doubleValue;
}
