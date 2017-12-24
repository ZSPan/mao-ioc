package com.aiden.ico.core.injector;

import com.aiden.ico.core.annotation.DefaultImplAnnotation;
import com.aiden.ico.core.annotation.InjectAnnotation;
import com.aiden.ico.core.annotation.InstanceAnnotation;
import com.aiden.ico.core.annotation.NameAnnotation;
import com.aiden.ico.core.exception.InstanceNotFoundException;
import com.aiden.ico.core.exception.InstanceRepeatedException;
import com.aiden.ico.core.helper.CircularDependencyHelper;
import com.aiden.ico.core.helper.ConstructorInjectHelper;
import com.aiden.ico.core.helper.FieldInjectHelper;
import com.aiden.ico.core.helper.LogHelper;
import com.aiden.ico.core.helper.ProviderInjectHelper;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

/**
 * @author yemingfeng
 */
@Data
@Slf4j
public class MaoInjector {

  private Map<Class<?>, Set<InstanceItem>> instanceItemMap;
  private Set<Class<?>> instanceClasses;
  private Reflections reflections;

  private CircularDependencyHelper circularDependencyHelper;
  private ConstructorInjectHelper constructorInjectHelper;
  private ProviderInjectHelper providerInjectHelper;
  private FieldInjectHelper fieldInjectHelper;

  public MaoInjector(Class<?> baseClass) {
    this.instanceItemMap = new HashMap<>();
    reflections = new Reflections(baseClass.getPackage().getName());
    instanceClasses = reflections.getTypesAnnotatedWith(InstanceAnnotation.class);

    LogHelper.logSegmentingLine();

    log.info("start inject");
    circularDependencyHelper = new CircularDependencyHelper(this);
    constructorInjectHelper = new ConstructorInjectHelper(this);
    providerInjectHelper = new ProviderInjectHelper(this);
    fieldInjectHelper = new FieldInjectHelper(this);
    LogHelper.logSegmentingLine();
    log.info("inject: {}", instanceItemMap);
    log.info("end inject");
    LogHelper.logSegmentingLine();
  }

  @SuppressWarnings("unchecked")
  public <T> T getInstance(Class<T> instanceClass) {
    Set<InstanceItem> items = instanceItemMap.get(instanceClass);
    if (items == null || items.size() == 0) {
      throw new InstanceNotFoundException(instanceClass);
    }
    if (items.size() > 1) {
      throw new InstanceRepeatedException(instanceClass);
    }
    return (T) items.iterator().next().getInstance();
  }

  public <T> T getInstance(NameAnnotation nameAnnotation,
      DefaultImplAnnotation defaultImplAnnotation,
      Class<?> instanceClass) {
    T object = getInstanceWithoutException(nameAnnotation, defaultImplAnnotation, instanceClass);
    if (object == null) {
      throw new InstanceNotFoundException(instanceClass);
    }
    return object;
  }

  @SuppressWarnings("unchecked")
  public <T> T getInstanceWithoutException(Class<?> instanceClass) {
    Set<InstanceItem> items = instanceItemMap.get(instanceClass);
    if (items == null || items.size() == 0) {
      return null;
    }
    return (T) items.iterator().next().getInstance();
  }

  @SuppressWarnings("unchecked")
  public <T> T getInstanceWithoutException(Class<?> instanceClass, String name) {
    Set<InstanceItem> items = instanceItemMap.get(instanceClass);
    for (InstanceItem item : items) {
      if (item.getName().equals(name)) {
        return (T) item.getInstance();
      }
    }
    return null;
  }

  public <T> T getInstanceWithoutException(NameAnnotation nameAnnotation,
      DefaultImplAnnotation defaultImplAnnotation,
      Class<?> instanceClass) {
    String defaultName = Optional.ofNullable(nameAnnotation)
        .map(NameAnnotation::name)
        .orElse(null);
    Class<?> needClass = Optional.ofNullable(defaultImplAnnotation)
        .map(_defaultImpl -> _defaultImpl.defaultImpl() == DefaultImplAnnotation.class ?
            instanceClass : _defaultImpl.defaultImpl())
        .orElse(instanceClass);
    if (defaultName != null) {
      return getInstanceWithoutException(needClass, defaultName);
    }
    return getInstanceWithoutException(needClass);
  }

  public <T> void putInstance(Class<T> instanceClass, Object instance, String name) {
    Set<Class<?>> superClasses = getSuperClasses(instanceClass);
    for (Class<?> superClass : superClasses) {
      Set<InstanceItem> items = instanceItemMap.computeIfAbsent(superClass,
          _superClass -> new HashSet<>());
      InstanceItem item = new InstanceItem(name, instanceClass, instance);
      if (items.contains(item)) {
        throw new InstanceRepeatedException(superClass);
      }
      items.add(item);
    }
  }

  private Set<Class<?>> getSuperClasses(Class<?> instanceClass) {
    Set<Class<?>> superClasses = new HashSet<>();
    superClasses.add(instanceClass);

    Stack<Class<?>> stack = new Stack<>();
    stack.add(instanceClass);

    while (!stack.isEmpty()) {
      Class<?> popClass = stack.pop();
      Class<?> superClass = popClass.getSuperclass();
      if (superClass != null && !superClass.equals(Object.class)) {
        superClasses.add(superClass);
        stack.add(superClass);
      }
      Class<?>[] superInterfaces = popClass.getInterfaces();
      for (Class<?> superInterface : superInterfaces) {
        superClasses.add(superInterface);
        stack.add(superInterface);
      }
    }
    return superClasses;
  }

  public Constructor<?> getConstructor(Class<?> instanceClass) {
    if (instanceClass.getAnnotation(InstanceAnnotation.class) == null) {
      throw new InstanceNotFoundException(instanceClass);
    }
    Constructor<?>[] constructors = instanceClass.getConstructors();
    Constructor<?> defaultConstructor = null;
    for (Constructor<?> constructor : constructors) {
      if (constructor.getAnnotation(InjectAnnotation.class) != null) {
        return constructor;
      }
      if (constructor.getParameters().length == 0) {
        defaultConstructor = constructor;
      }
    }
    return defaultConstructor;
  }

  @SuppressWarnings("unchecked")
  public <T> Set<Class<? extends T>> getSubClasses(Class<?> instanceClass) {
    return reflections.getSubTypesOf((Class<T>) instanceClass);
  }

  @Data
  @AllArgsConstructor
  private class InstanceItem {

    private String name;
    private Class<?> realClass;
    private Object instance;

    @Override
    public boolean equals(Object o) {
      return o instanceof InstanceItem && ((InstanceItem) o).getName().equals(name);
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }

    @Override
    public String toString() {
      return String.format("InstanceItem{name: %s, realClass: %s, instance: %s}\n",
          name, realClass, instance);
    }
  }
}