package com.aiden.ico.core.exception;

import java.lang.reflect.Method;

/**
 * @author yemingfeng
 */
public class ProviderInvokeException extends RuntimeException {

  public ProviderInvokeException(Method method, Exception e) {
    super("invoke " + method + " error", e);
  }
}