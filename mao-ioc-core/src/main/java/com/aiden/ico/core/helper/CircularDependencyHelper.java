package com.aiden.ico.core.helper;

import com.aiden.ico.core.exception.CircularDependencyException;
import com.aiden.ico.core.injector.MaoInjector;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
public class CircularDependencyHelper extends AbsInjectHelper {

  private Set<Class<?>> instanceClass;
  private Map<Class<?>, Set<Class<?>>> dependencies;

  public CircularDependencyHelper(MaoInjector injector) {
    super(injector);
  }

  @Override
  protected void doInit() {
    instanceClass = injector.getInstanceClasses();
    dependencies = new HashMap<>();
  }

  @Override
  protected void doWork() {
    LogHelper.logSegmentingLine();
    log.info("start analyze circular dependencies");
    for (Class<?> target : instanceClass) {
      Set<Class<?>> dependencyClasses = new HashSet<>();
      dependencies.put(target, dependencyClasses);
      appendDependencies(target, dependencyClasses);
      log.info("{} dependencies {}", target, dependencies.get(target));
    }
    log.info("end analyze circular dependencies");
  }

  private void appendDependencies(Class<?> target, Set<Class<?>> dependencyClasses) {
    Constructor<?> constructor = ClassHelper.getConstructor(target);
    List<Class<?>> parameterTypes = ClassHelper.getParameters(constructor);
    for (Class<?> parameterType : parameterTypes) {
      checkCircularDependency(parameterType, target);
      dependencyClasses.add(parameterType);
    }
    for (Class<?> parameterClass : parameterTypes) {
      appendDependencies(parameterClass, dependencyClasses);
    }
  }

  private void checkCircularDependency(Class<?> target, Class<?> dependencyClass) {
    Set<Class<?>> dependencyClasses = dependencies.get(dependencyClass);
    if (dependencyClasses != null && dependencyClasses.contains(target)) {
      throw new CircularDependencyException(dependencyClass, target);
    }
  }
}