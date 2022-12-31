package com.skibyte.demo;

import com.skibyte.demo.dto.ComplexMapField;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
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
import java.time.LocalDateTime;
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

    public static final Set<Class<?>> SUPPORTED_CLASSES = Set.of(Integer.class, Long.class, String.class, LocalDateTime.class, Boolean.class, Double.class);

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

                registerBean(aClass, itemProvider);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void registerBean(Class<?> aClass, ItemProvider<?> itemProvider) {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(ItemProvider.class, aClass);
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setTargetType(resolvableType);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinition.setAutowireCandidate(true);

        String beanName = ItemProvider.class.getName() + itemProvider.hashCode();

        DefaultListableBeanFactory bf = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
        bf.registerBeanDefinition(beanName, beanDefinition);
        bf.registerSingleton(beanName, itemProvider);
    }

    private <T> ItemProvider<T> create(Class<T> tClass) {
        Map<String, Method> allGet = getSupportedMethods(tClass, Strings.EMPTY);

        return new ItemProvider<T>() {
            @Override
            public Map<String, ComplexMapField<?>> convert(T source) {
                Map<String, ComplexMapField<?>> objectObjectHashMap = new HashMap<>();
                allGet.entrySet()
                        .forEach(e -> {
                            try {
                                Method method = e.getValue();
                                Class<?> returnType = method.getReturnType();
                                String[] split = e.getKey().split("\\.");
                                String[] internalGetters = Arrays.copyOf(split, split.length - 1);
                                for (String iGet : internalGetters) {
                                    Method internalGetter = allGet.get(iGet);
                                    Object internalObject = internalGetter.invoke(source);
                                    objectObjectHashMap.put(e.getKey(), new ComplexMapField(returnType, method.invoke(internalObject)));
                                }
                                if (internalGetters.length == 0 && SUPPORTED_CLASSES.contains(returnType)) {
                                    objectObjectHashMap.put(e.getKey(), new ComplexMapField(returnType, method.invoke(source)));
                                }
                            } catch (InvocationTargetException | IllegalAccessException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                return objectObjectHashMap;
            }
        };
    }

    private <T> Map<String, Method> getSupportedMethods(Class<T> tClass, String prefix) {
        Map<String, Method> allGet = getGetterMethodByFiledName(tClass);
        Map<String, Method> supportedGet = new HashMap<>();
        allGet.entrySet().forEach(e -> {
            Method method = e.getValue();
            Class<?> returnType = method.getReturnType();

            if (!SUPPORTED_CLASSES.contains(returnType)) {
                String baseKey = e.getKey() + ".";
                Class<?> innerDto = e.getValue().getReturnType();
                Map<String, Method> supportedMethods = getSupportedMethods(innerDto, baseKey);
                supportedGet.putAll(supportedMethods);
                supportedGet.put(e.getKey(), e.getValue());
            }
            supportedGet.put(prefix + e.getKey(), e.getValue());
        });
        return supportedGet;
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
