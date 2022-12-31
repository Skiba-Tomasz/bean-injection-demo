package com.skibyte.demo.dto;

import com.skibyte.demo.MapGenericConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NestedDto {
    private Long nestedLongValue;
    private Double nestedDoubleValue;
    private Nested2Dto nested2Dto;
}
