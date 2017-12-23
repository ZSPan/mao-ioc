package com.aiden.ico.core.helper;

import com.aiden.ico.core.injector.MaoInjector;

/**
 * @author yemingfeng
 */
public abstract class AbsInjectHelper {

  MaoInjector injector;

  AbsInjectHelper(MaoInjector injector) {
    this.injector = injector;
    doInit();
    doWork();
  }

  protected abstract void doInit();

  protected abstract void doWork();
}
