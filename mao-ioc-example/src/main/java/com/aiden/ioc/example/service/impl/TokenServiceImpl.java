package com.aiden.ioc.example.service.impl;

import com.aiden.ico.core.annotation.InjectAnnotation;
import com.aiden.ico.core.annotation.InstanceAnnotation;
import com.aiden.ioc.example.entity.User;
import com.aiden.ioc.example.service.TokenService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yemingfeng
 */
@Slf4j
@InstanceAnnotation
public class TokenServiceImpl implements TokenService {

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
}
