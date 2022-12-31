package com.skibyte.demo.dto;

import com.skibyte.demo.MapGenericConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@MapGenericConverter
public class ExampleDto {
    private Integer id;
    private String name;
    private LocalDateTime created;
    private Boolean checked;
}
