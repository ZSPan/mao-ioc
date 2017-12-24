package com.aiden.ico.core.exception;

/**
 * @author yemingfeng
 */
public class InstanceRepeatedException extends RuntimeException {

  public InstanceRepeatedException(Class<?> instanceClass) {
    super(instanceClass + " have more than one instances");
  }
}