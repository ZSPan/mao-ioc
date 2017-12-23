package com.aiden.ico.core.exception;

/**
 * @author yemingfeng
 */
public class InstanceNotFoundException extends RuntimeException {

  public InstanceNotFoundException(Class<?> target) {
    super(target + " not found");
  }
}