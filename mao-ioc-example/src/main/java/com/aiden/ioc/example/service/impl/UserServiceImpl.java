package com.aiden.ioc.example.service.impl;

import com.aiden.ico.core.annotation.DefaultImplAnnotation;
import com.aiden.ico.core.annotation.InjectAnnotation;
import com.aiden.ico.core.annotation.InstanceAnnotation;
import com.aiden.ioc.example.dao.UserDao;
import com.aiden.ioc.example.dao.impl.MysqlUserDaoImpl;
import com.aiden.ioc.example.service.TokenService;
import com.aiden.ioc.example.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
@InstanceAnnotation
public class UserServiceImpl implements UserService {

  @InjectAnnotation
  @DefaultImplAnnotation(defaultImpl = MysqlUserDaoImpl.class)
  private UserDao userDao;

  @InjectAnnotation
  private TokenService tokenService;

  @Override
  public void save() {
    userDao.save();
    tokenService.save();
  }

  @Override
  public void delete() {
    userDao.delete();
    tokenService.delete();
  }
}
