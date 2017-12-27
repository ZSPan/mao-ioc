package com.aiden.ico.core.helper;

import com.aiden.ico.core.annotation.DefaultImplAnnotation;
import com.aiden.ico.core.annotation.InjectAnnotation;
import com.aiden.ico.core.annotation.NameAnnotation;
import com.aiden.ico.core.exception.InjectFieldException;
import com.aiden.ico.core.injector.MaoInjector;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
public class FieldInjectHelper extends AbsInjectHelper {

  private List<Class<?>> instanceClasses;

  public FieldInjectHelper(MaoInjector injector) {
    super(injector);
  }

  @Override
  protected void doInit() {
    this.instanceClasses = injector.getInstanceClasses();
  }

  @Override
  protected void doWork() {
    LogHelper.logSegmentingLine();
    log.info("start inject field");
    for (Class<?> instanceClass : instanceClasses) {
      List<Field> fields = Arrays.asList(instanceClass.getDeclaredFields());
      fields.forEach(this::injectField);
    }
    log.info("end inject field");
  }

  private void injectField(Field field) {
    InjectAnnotation injectAnnotation = field.getAnnotation(InjectAnnotation.class);
    if (injectAnnotation == null) {
      return;
    }
    Object instance = injector.getInstance(field.getDeclaringClass());
    field.setAccessible(true);
    NameAnnotation nameAnnotation = field.getAnnotation(NameAnnotation.class);
    DefaultImplAnnotation defaultImplAnnotation = field.getAnnotation(DefaultImplAnnotation.class);
    try {
      Object fieldObject = injector.getInstance(nameAnnotation,
          defaultImplAnnotation, field.getType());
      field.set(instance, fieldObject);
      log.info("inject {} to {}", fieldObject, instance);
    } catch (IllegalAccessException e) {
      throw new InjectFieldException(field.getDeclaringClass(), field.getType(), e);
    }
  }
}