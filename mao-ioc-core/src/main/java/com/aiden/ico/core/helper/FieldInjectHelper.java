package com.aiden.ico.core.helper;

import com.aiden.ico.core.exception.InjectFieldException;
import com.aiden.ico.core.injector.MaoInjector;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
public class FieldInjectHelper extends AbsInjectHelper {

  private Set<Class<?>> instanceClass;

  public FieldInjectHelper(MaoInjector injector) {
    super(injector);
  }

  @Override
  protected void doInit() {
    this.instanceClass = injector.getInstanceClasses();
  }

  @Override
  protected void doWork() {
    LogHelper.logSegmentingLine();
    log.info("start inject field");
    for (Class<?> target : instanceClass) {
      List<Field> fields = ClassHelper.getField(target);
      for (Field field : fields) {
        injectField(field);
      }
    }
    log.info("end inject field");
  }

  private void injectField(Field field) {
    if (field.getAnnotation(Inject.class) == null) {
      return;
    }
    Class<?> target = field.getDeclaringClass();
    Object targetObject = injector.getInstance(field.getDeclaringClass());
    field.setAccessible(true);
    Class<?> fieldClass = field.getType();
    try {
      field.set(targetObject, injector.getInstance(fieldClass));
      log.info("inject {} to {}", fieldClass, target);
    } catch (IllegalAccessException e) {
      throw new InjectFieldException(target, fieldClass, e);
    }
  }
}