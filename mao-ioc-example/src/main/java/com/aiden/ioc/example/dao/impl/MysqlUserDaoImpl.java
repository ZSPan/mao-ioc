package com.aiden.ioc.example.dao.impl;

import com.aiden.ico.core.annotation.InjectAnnotation;
import com.aiden.ico.core.annotation.InstanceAnnotation;
import com.aiden.ioc.example.dao.UserDao;
import com.aiden.ioc.example.entity.User;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
@InstanceAnnotation(name = "mysqlUserDaoImpl")
public class MysqlUserDaoImpl implements UserDao {

  @InjectAnnotation
  private User user;

  @Override
  public void save() {
    log.info("mysql save: {}", user);
  }

  @Override
  public void delete() {
    log.info("mysql delete: {}", user);
  }
}
