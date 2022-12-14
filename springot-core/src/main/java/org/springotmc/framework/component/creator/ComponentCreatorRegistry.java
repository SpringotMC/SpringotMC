package org.springotmc.framework.component.creator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springotmc.framework.component.manifest.BeanManifest;
import org.springotmc.framework.component.manifest.BeanSource;
import org.springotmc.injection.Injector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@RequiredArgsConstructor
public class ComponentCreatorRegistry {

    @NonNull private final Injector injector;
    private final List<ComponentResolver> componentResolvers = new ArrayList<>();
    private final Set<Class<?>> dynamicTypes = new HashSet<>();
    private final Set<Class<? extends Annotation>> dynamicAnnotations = new HashSet<>();

    public ComponentCreatorRegistry register(Class<? extends ComponentResolver> componentResolverType) {
        return this.register(this.injector.createInstance(componentResolverType));
    }

    public ComponentCreatorRegistry register(ComponentResolver componentResolver) {
        this.componentResolvers.add(componentResolver);
        return this;
    }

    public ComponentCreatorRegistry registerDynamicType(Class<?> type) {
        this.dynamicTypes.add(type);
        return this;
    }

    public ComponentCreatorRegistry registerDynamicAnnotation(Class<? extends Annotation> type) {
        this.dynamicAnnotations.add(type);
        return this;
    }

    public boolean isDynamicType(Class<?> dynamicType) {
        return this.dynamicTypes.contains(dynamicType);
    }

    public boolean isDynamicAnnotation(Class<? extends Annotation> annotationType) {
        return this.dynamicTypes.contains(annotationType);
    }

    @SuppressWarnings({"Convert2MethodRef"})
    public boolean isDynamicParameter(Parameter parameter) {

        // check type
        if (this.isDynamicType(parameter.getType())) {
            return true;
        }

        // check if any of annotations is marked as dynamic
        return this.dynamicAnnotations.stream()
            .map(type -> parameter.getAnnotation(type))
            .anyMatch(Objects::nonNull);
    }

    public boolean supports(Class<?> type) {
        return this.componentResolvers.stream().anyMatch(resolver -> resolver.supports(type));
    }

    public boolean supports(Method method) {
        return this.componentResolvers.stream().anyMatch(resolver -> resolver.supports(method));
    }

    public Optional<Object> make(ComponentCreator creator, BeanManifest manifest) {

        if (manifest.getSource() == BeanSource.COMPONENT) {
            return this.componentResolvers.stream()
                .filter(resolver -> resolver.supports(manifest.getType()))
                .map(resolver -> resolver.make(creator, manifest, this.injector))
                .findAny();
        }

        if (manifest.getSource() == BeanSource.METHOD) {
            return this.componentResolvers.stream()
                .filter(resolver -> resolver.supports(manifest.getMethod()))
                .map(resolver -> resolver.make(creator, manifest, this.injector))
                .findAny();
        }

        throw new IllegalArgumentException("Unsupported manifest source: " + manifest.getSource());
    }
}
