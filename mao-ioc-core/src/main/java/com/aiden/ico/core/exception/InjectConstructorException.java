package com.aiden.ico.core.exception;

/**
 * @author yemingfeng
 */
public class InjectConstructorException extends RuntimeException {

  public InjectConstructorException(Class<?> instanceClass, Exception e) {
    super("inject " + instanceClass + " failed", e);
  }
}