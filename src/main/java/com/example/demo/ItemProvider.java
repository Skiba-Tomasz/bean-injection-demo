package com.example.demo;

import org.springframework.core.convert.converter.Converter;

import java.util.Map;

public interface ItemProvider<T> extends Converter<T, Map<String, ComplexMapField<?>>> {

}
