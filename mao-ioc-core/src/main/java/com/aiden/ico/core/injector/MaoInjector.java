package com.aiden.ico.core.injector;

import com.aiden.ico.core.annotation.Instance;
import com.aiden.ico.core.exception.InstanceNotFoundException;
import com.aiden.ico.core.exception.InstanceRepeatedException;
import com.aiden.ico.core.helper.CircularDependencyHelper;
import com.aiden.ico.core.helper.ClassHelper;
import com.aiden.ico.core.helper.ConstructorInjectHelper;
import com.aiden.ico.core.helper.FieldInjectHelper;
import com.aiden.ico.core.helper.LogHelper;
import com.aiden.ico.core.helper.ProviderInjectHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

  private CircularDependencyHelper circularDependencyHelper;
  private ConstructorInjectHelper constructorInjectHelper;
  private ProviderInjectHelper providerInjectHelper;
  private FieldInjectHelper fieldInjectHelper;

  public MaoInjector(Class<?> baseClass) {
    this.instanceItemMap = new HashMap<>();
    Reflections reflections = new Reflections(baseClass.getPackage().getName());
    instanceClasses = reflections.getTypesAnnotatedWith(Instance.class);

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
  public <T> T getInstance(Class<T> target) {
    Set<InstanceItem> items = instanceItemMap.get(target);
    if (items == null || items.size() == 0) {
      throw new InstanceNotFoundException(target);
    }
    if (items.size() > 1) {
      throw new InstanceRepeatedException(target);
    }
    return (T) items.iterator().next().getRealObject();
  }

  @SuppressWarnings("unchecked")
  public <T> T getInstance(Class<?> target, String name) {
    Set<InstanceItem> items = instanceItemMap.get(target);
    if (items == null || items.size() == 0) {
      throw new InstanceNotFoundException(target);
    }
    for (InstanceItem item : items) {
      if (item.getName().equals(name)) {
        return (T) item.getRealObject();
      }
    }
    throw new InstanceNotFoundException(target);
  }

  @SuppressWarnings("unchecked")
  public <T> T getInstanceWithoutException(Class<?> target) {
    Set<InstanceItem> items = instanceItemMap.get(target);
    if (items == null || items.size() == 0) {
      return null;
    }
    return (T) items.iterator().next().getRealObject();
  }

  public <T> void putInstance(Class<T> target, Object instance, String name) {
    Set<Class<?>> superClasses = ClassHelper.getSuperClasses(target);
    for (Class<?> superClass : superClasses) {
      Set<InstanceItem> items = instanceItemMap.computeIfAbsent(superClass,
          _superClass -> new HashSet<>());
      InstanceItem item = new InstanceItem(name, target, instance);
      if (items.contains(item)) {
        throw new InstanceRepeatedException(superClass);
      }
      items.add(item);
    }
  }

  @Data
  @AllArgsConstructor
  private class InstanceItem {

    private String name;
    private Class<?> realClass;
    private Object realObject;

    @Override
    public boolean equals(Object o) {
      return o instanceof InstanceItem && ((InstanceItem) o).getName().equals(name);
    }

    @Override
    public String toString() {
      return String.format("InstanceItem{name: %s, realClass: %s, realObject: %s}\n",
          name, realClass, realObject);
    }
  }
}