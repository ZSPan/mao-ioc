package com.aiden.ioc.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author yemingfeng
 */
@Data
@AllArgsConstructor
public class User {

  private String username;
  private String password;
}