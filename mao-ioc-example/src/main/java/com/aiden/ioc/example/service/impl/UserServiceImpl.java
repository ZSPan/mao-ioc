package com.aiden.ioc.example.service.impl;

import com.aiden.ico.core.annotation.Inject;
import com.aiden.ico.core.annotation.Instance;
import com.aiden.ioc.example.dao.UserDao;
import com.aiden.ioc.example.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
@Instance
public class UserServiceImpl implements UserService {

  @Inject
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
