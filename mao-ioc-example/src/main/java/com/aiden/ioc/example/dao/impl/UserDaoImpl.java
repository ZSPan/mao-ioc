package com.aiden.ioc.example.dao.impl;

import com.aiden.ico.core.annotation.InjectAnnotation;
import com.aiden.ico.core.annotation.InstanceAnnotation;
import com.aiden.ico.core.annotation.ProviderAnnotation;
import com.aiden.ioc.example.dao.UserDao;
import com.aiden.ioc.example.entity.User;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
@InstanceAnnotation
public class UserDaoImpl implements UserDao {

  @InjectAnnotation
  private User user;

  @Override
  public void save() {
    log.info("save: {}", user);
  }

  @Override
  public void delete() {
    log.info("delete: {}", user);
  }

  @ProviderAnnotation
  public User providerUser() {
    User user = new User();
    user.setPassword("123456");
    user.setUsername("yemingfeng");
    return user;
  }
}