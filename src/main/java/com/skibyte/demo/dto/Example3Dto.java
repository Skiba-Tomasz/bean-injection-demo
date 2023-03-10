package com.skibyte.demo.dto;

import com.skibyte.demo.MapGenericConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MapGenericConverter
public class Example3Dto {
    private Long longValue;
    private Double doubleValue;
    private NestedDto nestedDto;
}
