package com.aiden.ico.core.helper;

import com.aiden.ico.core.annotation.DefaultImplAnnotation;
import com.aiden.ico.core.annotation.InstanceAnnotation;
import com.aiden.ico.core.annotation.NameAnnotation;
import com.aiden.ico.core.exception.ClassNotContainInstanceAnnotationException;
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

  private Set<Class<?>> instanceClasses;

  public ConstructorInjectHelper(MaoInjector injector) {
    super(injector);
  }

  @Override
  protected void doInit() {
    this.instanceClasses = injector.getInstanceClasses();
  }

  @Override
  protected void doWork() {
    LogHelper.logSegmentingLine();
    log.info("start inject constructor");
    for (Class<?> instanceClass : instanceClasses) {
      injectConstructor(injector.getConstructor(instanceClass));
    }
    log.info("end inject constructor");
  }

  private void injectConstructor(Constructor<?> instanceConstructor) {
    Class<?> instanceClass = instanceConstructor.getDeclaringClass();
    InstanceAnnotation instanceAnnotation =
        instanceClass.getDeclaredAnnotation(InstanceAnnotation.class);
    List<Object> params = getParamObjects(instanceConstructor.getParameters());
    try {
      Object instance = instanceConstructor.newInstance(params.toArray(new Object[0]));
      injector.putInstance(instanceClass, instance, instanceAnnotation.name());
      log.info("inject {} to {}", params, instance);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new InjectConstructorException(instanceClass, e);
    }
  }

  private List<Object> getParamObjects(Parameter[] parameters) {
    List<Object> instances = new ArrayList<>();
    for (Parameter parameter : parameters) {
      NameAnnotation nameAnnotation = parameter.getAnnotation(NameAnnotation.class);
      DefaultImplAnnotation defaultImplAnnotation = parameter
          .getAnnotation(DefaultImplAnnotation.class);
      Class<?> instanceClass = parameter.getType();
      Object object = injector.getInstanceWithoutException(nameAnnotation,
          defaultImplAnnotation, instanceClass);
      if (object == null) {
        initInstance(instanceClass);
        object = injector.getInstance(nameAnnotation, defaultImplAnnotation, instanceClass);
      }
      instances.add(object);
    }
    return instances;
  }

  private void initInstance(Class<?> instanceClass) {
    List<Class<?>> subClasses = new ArrayList<>();
    subClasses.add(instanceClass);
    subClasses.addAll(injector.getSubClasses(instanceClass));

    Class<?> defaultSubClass = null;
    for (Class<?> subClass : subClasses) {
      if (instanceClass.getAnnotation(InstanceAnnotation.class) != null &&
          !instanceClass.isInterface() &&
          !instanceClass.isAnnotation()) {
        defaultSubClass = subClass;
      }
    }
    if (defaultSubClass == null) {
      throw new ClassNotContainInstanceAnnotationException(instanceClass);
    }
    injectConstructor(injector.getConstructor(instanceClass));
  }
}