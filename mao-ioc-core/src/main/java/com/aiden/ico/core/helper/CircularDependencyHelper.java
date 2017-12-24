package com.aiden.ico.core.helper;

import com.aiden.ico.core.exception.CircularDependencyException;
import com.aiden.ico.core.injector.MaoInjector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
public class CircularDependencyHelper extends AbsInjectHelper {

  private Set<Class<?>> instanceClasses;
  private Map<Class<?>, Set<Class<?>>> dependencies;

  public CircularDependencyHelper(MaoInjector injector) {
    super(injector);
  }

  @Override
  protected void doInit() {
    instanceClasses = injector.getInstanceClasses();
    dependencies = new HashMap<>();
  }

  @Override
  protected void doWork() {
    LogHelper.logSegmentingLine();
    log.info("start analyze circular dependencies");
    for (Class<?> instanceClass : instanceClasses) {
      Set<Class<?>> dependencyClasses = dependencies.computeIfAbsent(
          instanceClass, _instanceClass -> new HashSet<>());
      appendDependencies(instanceClass, dependencyClasses);
      log.info("{} dependencies {}", instanceClass, dependencyClasses);
    }
    log.info("end analyze circular dependencies");
  }

  private void appendDependencies(Class<?> instanceClass, Set<Class<?>> dependencyClasses) {
    Constructor<?> constructor = injector.getConstructor(instanceClass);
    List<Class<?>> parameterTypes = getParameters(constructor);
    for (Class<?> parameterType : parameterTypes) {
      checkCircularDependency(parameterType, instanceClass);
      dependencyClasses.add(parameterType);
      appendDependencies(parameterType, dependencyClasses);
    }
  }

  private void checkCircularDependency(Class<?> parameterType, Class<?> instanceClass) {
    if (dependencies.computeIfAbsent(parameterType, _dependencyClass -> new HashSet<>())
        .contains(instanceClass)) {
      throw new CircularDependencyException(parameterType, instanceClass);
    }
  }

  private List<Class<?>> getParameters(Executable executable) {
    return Arrays.stream(executable.getParameters())
        .map((Function<Parameter, Class<?>>) Parameter::getType)
        .collect(Collectors.toList());
  }
}