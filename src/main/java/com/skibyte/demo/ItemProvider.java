package com.skibyte.demo;

import com.skibyte.demo.dto.ComplexMapField;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

public interface ItemProvider<T> extends Converter<T, Map<String, ComplexMapField<?>>> {

}
