package com.aiden.ioc.example.dao.impl;

import com.aiden.ico.anno.Instance;
import com.aiden.ioc.example.dao.UserDao;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
@Instance
public class UserDaoImpl implements UserDao {

  @Override
  public void save() {
    log.info("save");
  }

  @Override
  public void delete() {
    log.info("delete");
  }
}