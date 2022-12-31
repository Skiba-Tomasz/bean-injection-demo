package com.skibyte.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MapConverter
public class ExampleDto {
    private Integer id;
    private String name;
    private LocalDateTime created;
    private Boolean checked;
}
