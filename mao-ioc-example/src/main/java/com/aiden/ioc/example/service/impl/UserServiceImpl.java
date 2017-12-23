package com.aiden.ioc.example.service.impl;

import com.aiden.ico.anno.Instance;
import com.aiden.ioc.example.dao.UserDao;
import com.aiden.ioc.example.service.UserService;
import javax.inject.Inject;
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
