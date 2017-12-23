package com.aiden.ico.core.exception;

/**
 * @author yemingfeng
 */
public class InjectConstructorException extends RuntimeException {

  public InjectConstructorException(Class<?> target, Exception e) {
    super("inject " + target + " failed", e);
  }
}