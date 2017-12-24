package com.aiden.ico.core.helper;

import com.aiden.ico.core.annotation.Inject;
import com.aiden.ico.core.annotation.Provider;
import com.google.common.collect.Sets;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yemingfeng
 */
public class ClassHelper {

  static Constructor<?> getConstructor(Class<?> targetClass) {
    Constructor<?>[] targetClassConstructors = targetClass.getConstructors();
    Constructor<?> defaultConstructor = null;
    for (Constructor<?> targetConstructor : targetClassConstructors) {
      if (targetConstructor.getAnnotation(Inject.class) != null) {
        return targetConstructor;
      }
      if (targetConstructor.getParameters().length == 0) {
        defaultConstructor = targetConstructor;
      }
    }
    return defaultConstructor;
  }

  static List<Field> getField(Class<?> clazz) {
    return Arrays.asList(clazz.getDeclaredFields());
  }

  private static Set<Class<?>> getDirectSuperClass(Class<?> target) {
    Set<Class<?>> superClasses = Sets.newHashSet();
    Class<?> superClass = target.getSuperclass();
    if (superClass != null && superClass != Object.class) {
      superClasses.add(target.getSuperclass());
    }
    superClasses.addAll(Arrays.stream(target.getInterfaces())
        .filter(Objects::nonNull)
        .filter(aClass -> aClass != Object.class)
        .collect(Collectors.toList())
    );
    return superClasses;
  }

  public static Set<Class<?>> getSuperClasses(Class<?> target) {
    Set<Class<?>> superClasses = Sets.newHashSet();
    superClasses.add(target);
    Stack<Class<?>> stack = new Stack<>();
    Class<?> currentClass = target;
    Set<Class<?>> currentClassSuperClasses = getDirectSuperClass(currentClass);
    superClasses.addAll(currentClassSuperClasses);
    stack.addAll(currentClassSuperClasses);
    while (!stack.isEmpty()) {
      currentClass = stack.pop();
      currentClassSuperClasses = getDirectSuperClass(currentClass);
      superClasses.addAll(currentClassSuperClasses);
      stack.addAll(currentClassSuperClasses);
    }
    return superClasses;
  }

  static Set<Method> getProviderMethods(Class<?> target) {
    Method[] providerMethods = target.getDeclaredMethods();
    return Sets.newHashSet(providerMethods)
        .stream()
        .filter(method -> method.getAnnotation(Provider.class) != null)
        .collect(Collectors.toSet());
  }

  static List<Class<?>> getParameters(Executable executable) {
    return Arrays.stream(executable.getParameters())
        .map((Function<Parameter, Class<?>>) Parameter::getType)
        .collect(Collectors.toList());
  }
}