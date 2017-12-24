package com.aiden.ico.core.exception;

/**
 * @author yemingfeng
 */
public class ClassNotContainInstanceAnnotationException extends RuntimeException {

  public ClassNotContainInstanceAnnotationException(Class<?> instanceClass) {
    super(instanceClass + " not contain instance annotation");
  }
}