package com.example.demo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class ProviderBeanMaker {

    private final ConfigurableApplicationContext applicationContext;

    @PostConstruct
    void setup() throws ClassNotFoundException {
        Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass(ExampleDto.class.getCanonicalName());
        ItemProvider<?> itemProvider = create(aClass);


        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(ItemProvider.class, aClass);
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setTargetType(resolvableType);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinition.setAutowireCandidate(true);


        DefaultListableBeanFactory bf = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
        bf.registerBeanDefinition("testBean", beanDefinition);
        bf.registerSingleton("testBean", itemProvider);

    }

    private <T> ItemProvider<T> create(Class<T> tClass){
        Map<String, Method> get = getGetterMethodByFiledName(tClass);
        return new ItemProvider<T>() {
            @Override
            public Map<String, ComplexMapField<?>> convert(T source) {
                Map<String, ComplexMapField<?>> objectObjectHashMap = new HashMap<>();
                get.entrySet()
                        .stream()
                        .forEach(e -> {
                            try {
                                Method method = e.getValue();
                                objectObjectHashMap.put(e.getKey(), new ComplexMapField(method.getReturnType(), method.invoke(source)));
                            } catch (InvocationTargetException | IllegalAccessException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                return objectObjectHashMap;
            }
        };
    }

    private <T> Map<String, Method> getGetterMethodByFiledName(Class<T> tClass) {
        return Arrays.stream(tClass.getDeclaredMethods())
                .filter(m -> m.getName().startsWith("get") || m.getName().startsWith("is"))
                .collect(Collectors.toMap(
                        this::getterNameToFieldName,
                        Function.identity()
                ));
    }

    private String getterNameToFieldName(Method m) {
        String substring = m.getName().replaceFirst("get|is", "");
        char[] chars = substring.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

}
