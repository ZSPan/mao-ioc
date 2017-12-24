package com.aiden.ioc.example.config;

import com.aiden.ico.core.annotation.InstanceAnnotation;
import com.aiden.ico.core.annotation.ProviderAnnotation;
import com.aiden.ioc.example.entity.User;

/**
 * @author yemingfeng
 */
@InstanceAnnotation
public class UserConfig {


  @ProviderAnnotation
  public User providerUser() {
    return new User("yemingfeng", "123456");
  }
}