package com.aiden.ico.core.exception;

/**
 * @author yemingfeng
 */
public class CircularDependencyException extends RuntimeException {

  public CircularDependencyException(Class<?> instanceClass, Class<?> dependencyClass) {
    super(dependencyClass + " " + instanceClass + " circularDependency");
  }
}