package com.aiden.ioc.example.service.impl;

import com.aiden.ico.core.annotation.InjectAnnotation;
import com.aiden.ico.core.annotation.InstanceAnnotation;
import com.aiden.ioc.example.dao.UserDao;
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
  private UserDao userDao;

  @InjectAnnotation
  private TokenService tokenService;

  @Override
  public void save() {
    log.info("start save");
    userDao.save();
    tokenService.save();
    log.info("end save");
  }

  @Override
  public void delete() {
    log.info("start delete");
    userDao.delete();
    userDao.save();
    log.info("end delete");
  }
}
