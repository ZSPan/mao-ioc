package com.aiden.ico.core.exception;

/**
 * @author yemingfeng
 */
public class InjectFieldException extends RuntimeException {

  public InjectFieldException(Class<?> target, Class<?> fieldClass, Exception e) {
    super("inject " + fieldClass + " to " + target + " failed", e);
  }
}