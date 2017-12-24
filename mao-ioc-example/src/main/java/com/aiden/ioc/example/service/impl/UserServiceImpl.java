package com.aiden.ioc.example.service.impl;

import com.aiden.ico.core.annotation.InjectAnnotation;
import com.aiden.ico.core.annotation.InstanceAnnotation;
import com.aiden.ioc.example.dao.UserDao;
import com.aiden.ioc.example.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
@InstanceAnnotation
public class UserServiceImpl implements UserService {

  @InjectAnnotation
  private UserDao userDao;

  @Override
  public void save() {
    userDao.save();
  }

  @Override
  public void delete() {
    userDao.delete();
  }
}
