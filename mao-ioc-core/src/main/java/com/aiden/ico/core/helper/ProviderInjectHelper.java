package com.aiden.ico.core.helper;

import com.aiden.ico.core.annotation.DefaultImplAnnotation;
import com.aiden.ico.core.annotation.NameAnnotation;
import com.aiden.ico.core.annotation.ProviderAnnotation;
import com.aiden.ico.core.exception.ProviderInvokeException;
import com.aiden.ico.core.injector.MaoInjector;
import com.google.common.collect.Sets;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
public class ProviderInjectHelper extends AbsInjectHelper {

  private Set<Class<?>> instanceClasses;

  public ProviderInjectHelper(MaoInjector injector) {
    super(injector);
  }

  @Override
  protected void doInit() {
    instanceClasses = injector.getInstanceClasses();
  }

  @Override
  protected void doWork() {
    LogHelper.logSegmentingLine();
    log.info("start inject provider");
    for (Class<?> instanceClass : instanceClasses) {
      Set<Method> providerMethods = getProviderMethods(instanceClass);
      for (Method providerMethod : providerMethods) {
        List<Object> parameters = Arrays.stream(providerMethod.getParameters())
            .map(parameter -> {
              NameAnnotation name = parameter.getAnnotation(NameAnnotation.class);
              DefaultImplAnnotation defaultImpl = parameter
                  .getAnnotation(DefaultImplAnnotation.class);
              return injector.getInstanceWithoutException(name, defaultImpl, parameter.getType());
            })
            .collect(Collectors.toList());
        try {
          ProviderAnnotation providerAnnotation =
              providerMethod.getAnnotation(ProviderAnnotation.class);
          Object instance = providerMethod.invoke(injector.getInstance(instanceClass),
              parameters.toArray(new Object[parameters.size()]));
          injector.putInstance(instance.getClass(), instance, providerAnnotation.name());
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new ProviderInvokeException(providerMethod, e);
        }
      }
    }
    log.info("end inject provider");
  }

  private Set<Method> getProviderMethods(Class<?> instanceClass) {
    Method[] providerMethods = instanceClass.getDeclaredMethods();
    return Sets.newHashSet(providerMethods)
        .stream()
        .filter(method -> method.getAnnotation(ProviderAnnotation.class) != null)
        .collect(Collectors.toSet());
  }
}