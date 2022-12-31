package com.skibyte.demo;

import com.skibyte.demo.dto.ComplexMapField;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class MapGenericConverterBeanFactory {

    public static final String BASE_PACKAGE = "com.skibyte.demo";

    private final ConfigurableApplicationContext applicationContext;

    @PostConstruct
    void setup() {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(MapGenericConverter.class));
        Set<BeanDefinition> beanDefs = provider
                .findCandidateComponents(BASE_PACKAGE);

        beanDefs.forEach(annotatedClass -> {
            try {
                String beanClassName = annotatedClass.getBeanClassName();
                Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass(beanClassName);

                ItemProvider<?> itemProvider = create(aClass);

                ResolvableType resolvableType = ResolvableType.forClassWithGenerics(ItemProvider.class, aClass);
                RootBeanDefinition beanDefinition = new RootBeanDefinition();
                beanDefinition.setTargetType(resolvableType);
                beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                beanDefinition.setAutowireCandidate(true);

                String beanName = ItemProvider.class.getName() + itemProvider.hashCode();

                DefaultListableBeanFactory bf = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
                bf.registerBeanDefinition(beanName, beanDefinition);
                bf.registerSingleton(beanName, itemProvider);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <T> ItemProvider<T> create(Class<T> tClass) {
        Map<String, Method> get = getGetterMethodByFiledName(tClass);
        return new ItemProvider<T>() {
            @Override
            public Map<String, ComplexMapField<?>> convert(T source) {
                Map<String, ComplexMapField<?>> objectObjectHashMap = new HashMap<>();
                get.entrySet()
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
