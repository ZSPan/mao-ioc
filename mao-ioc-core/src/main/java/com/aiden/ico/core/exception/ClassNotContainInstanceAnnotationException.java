package com.aiden.ico.core.exception;

/**
 * @author yemingfeng
 */
public class ClassNotContainInstanceAnnotationException extends RuntimeException {

  public ClassNotContainInstanceAnnotationException(Class<?> target) {
    super(target + " not contain instance annotation");
  }
}