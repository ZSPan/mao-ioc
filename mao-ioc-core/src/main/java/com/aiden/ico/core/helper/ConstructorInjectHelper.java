package com.aiden.ico.core.helper;

import com.aiden.ico.core.exception.InjectConstructorException;
import com.aiden.ico.core.injector.MaoInjector;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
public class ConstructorInjectHelper extends AbsInjectHelper {

  private Set<Class<?>> instanceClass;

  public ConstructorInjectHelper(MaoInjector injector) {
    super(injector);
  }

  @Override
  protected void doInit() {
    this.instanceClass = injector.getInstanceClasses();
  }

  @Override
  protected void doWork() {
    LogHelper.logSegmentingLine();
    log.info("start inject constructor");
    for (Class<?> target : instanceClass) {
      injectConstructor(ClassHelper.getConstructor(target));
    }
    log.info("end inject constructor");
  }

  private void injectConstructor(Constructor<?> targetConstructor) {
    Class<?> target = targetConstructor.getDeclaringClass();
    List<Object> params = getParamObjects(targetConstructor.getParameters());
    Object targetObject;
    try {
      targetObject = targetConstructor.newInstance(params.toArray(new Object[0]));
      log.info("inject {} to {}", params, targetObject);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new InjectConstructorException(target, e);
    }
    injector.putInstance(target, targetObject);
  }

  private List<Object> getParamObjects(Parameter[] parameters) {
    List<Object> instances = new ArrayList<>();
    for (Parameter parameter : parameters) {
      Class<?> parameterType = parameter.getType();
      Object object = injector.getInstanceWithoutException(parameterType);
      if (object == null) {
        injectConstructor(ClassHelper.getConstructor(parameterType));
        object = injector.getInstanceWithoutException(parameterType);
      }
      instances.add(object);
    }
    return instances;
  }
}