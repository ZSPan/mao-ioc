package com.aiden.ico.core.helper;

import com.aiden.ico.core.annotation.Provider;
import com.aiden.ico.core.exception.ProviderInvokeException;
import com.aiden.ico.core.injector.MaoInjector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    for (Class<?> targetClass : instanceClasses) {
      Set<Method> providerMethods = ClassHelper.getProviderMethods(targetClass);
      for (Method providerMethod : providerMethods) {
        List<Class<?>> parameterClasses = ClassHelper.getParameters(providerMethod);
        List<Object> parameters = parameterClasses.stream()
            .map(parameterClass -> injector.getInstance(parameterClass))
            .collect(Collectors.toList());
        try {
          Provider provider = providerMethod.getAnnotation(Provider.class);
          Object targetObject = providerMethod.invoke(injector.getInstance(targetClass),
              parameters.toArray(new Object[parameters.size()]));
          injector.putInstance(targetObject.getClass(), targetObject, provider.name());
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new ProviderInvokeException(providerMethod, e);
        }
      }
    }
    log.info("end inject provider");
  }
}