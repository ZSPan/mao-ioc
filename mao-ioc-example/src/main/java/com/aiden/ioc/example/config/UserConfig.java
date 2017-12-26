package com.aiden.ioc.example.config;

import com.aiden.ico.core.annotation.InstanceAnnotation;
import com.aiden.ico.core.annotation.ProviderAnnotation;
import com.aiden.ioc.example.entity.User;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
@InstanceAnnotation
public class UserConfig {


  @ProviderAnnotation
  public User providerUser() {
    return new User("yemingfeng", "123456");
  }

  @ProviderAnnotation
  public void consumeUser(User user) {
    log.info("{}", user);
  }
}