package com.aiden.ico.core.exception;

/**
 * @author yemingfeng
 */
public class CircularDependencyException extends RuntimeException {

  public CircularDependencyException(Class<?> target, Class<?> dependencyClass) {
    super(dependencyClass + " " + target + " circularDependency");
  }
}