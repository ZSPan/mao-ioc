package com.aiden.ico.core.exception;

/**
 * @author yemingfeng
 */
public class InstanceNotFoundException extends RuntimeException {

  public InstanceNotFoundException(Class<?> instanceClass) {
    super(instanceClass + " not found");
  }
}