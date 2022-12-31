package com.skibyte.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplexMapField<T> {
    private Class<T> clazz;
    private T value;
}
