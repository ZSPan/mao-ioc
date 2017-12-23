package com.aiden.ico.core.injector;

import com.aiden.ico.anno.Instance;
import com.aiden.ico.core.exception.InstanceNotFoundException;
import com.aiden.ico.core.exception.InstanceRepeatedException;
import com.aiden.ico.core.helper.CircularDependencyHelper;
import com.aiden.ico.core.helper.ClassHelper;
import com.aiden.ico.core.helper.ConstructorInjectHelper;
import com.aiden.ico.core.helper.FieldInjectHelper;
import com.aiden.ico.core.helper.LogHelper;
import com.aiden.ico.core.helper.ProviderInjectHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

/**
 * @author yemingfeng
 */
@Data
@Slf4j
public class MaoInjector {

  private Map<Class<?>, Object> maoInstances;
  private Set<Class<?>> instanceClasses;

  private CircularDependencyHelper circularDependencyHelper;
  private ConstructorInjectHelper constructorInjectHelper;
  private ProviderInjectHelper providerInjectHelper;
  private FieldInjectHelper fieldInjectHelper;

  public MaoInjector(Class<?> baseClass) {
    this.maoInstances = new HashMap<>();
    Reflections reflections = new Reflections(baseClass.getPackage().getName());
    instanceClasses = reflections.getTypesAnnotatedWith(Instance.class);

    LogHelper.logSegmentingLine();
    log.info("start inject");
    circularDependencyHelper = new CircularDependencyHelper(this);
    constructorInjectHelper = new ConstructorInjectHelper(this);
    providerInjectHelper = new ProviderInjectHelper(this);
    fieldInjectHelper = new FieldInjectHelper(this);
    log.info("end inject");
  }

  @SuppressWarnings("unchecked")
  public <T> T getInstance(Class<T> target) {
    Object instance = maoInstances.get(target);
    if (instance == null) {
      throw new InstanceNotFoundException(target);
    }
    return (T) instance;
  }

  @SuppressWarnings("unchecked")
  public <T> T getInstanceWithoutException(Class<?> target) {
    return (T) Optional.ofNullable(maoInstances.get(target))
        .orElse(null);
  }

  public <T> void putInstance(Class<T> target, Object instance) {
    Set<Class<?>> superClasses = ClassHelper.getSuperClasses(target);
    for (Class<?> superClass : superClasses) {
      if (maoInstances.containsKey(superClass)) {
        throw new InstanceRepeatedException(superClass);
      }
      maoInstances.put(superClass, instance);
      log.info("{} have instance: {}", superClass, instance);
    }
  }
}