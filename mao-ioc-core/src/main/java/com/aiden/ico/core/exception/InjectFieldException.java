package com.aiden.ico.core.exception;

/**
 * @author yemingfeng
 */
public class InjectFieldException extends RuntimeException {

  public InjectFieldException(Class<?> instanceClass, Class<?> fieldClass, Exception e) {
    super("inject " + fieldClass + " to " + instanceClass + " failed", e);
  }
}