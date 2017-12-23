package com.aiden.ico.core.exception;

/**
 * @author yemingfeng
 */
public class InstanceRepeatedException extends RuntimeException {

  public InstanceRepeatedException(Class<?> target) {
    super(target + " have more than one instances");
  }
}