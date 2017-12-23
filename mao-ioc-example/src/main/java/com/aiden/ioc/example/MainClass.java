package com.aiden.ioc.example;

import com.aiden.ico.core.injector.MaoInjector;
import com.aiden.ioc.example.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
public class MainClass {

  public static void main(String[] args) {
    MaoInjector injector = new MaoInjector(MainClass.class);
    UserService userService = injector.getInstance(UserService.class);
    userService.save();
  }
}