**Java 简板的依賴注入**

1.工具&类库&环境：
- SLF4J + Log4J
- Maven
- Reflections
- JDK8
- IDEA

例子:
```$java
package com.dao;
public interface UserDao {
}

package com.dao.impl;
@Instance
public class UserDaoImpl implements UserDao {
}

package com.service;
public interface UserService {
}

package com.service.impl;
@Instance
public class UserServiceImpl implements UserService {

    // @Inject
    // private UserDao;
    
    private UserDao userDao;
    @Inject
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }
}

package com;
public MainClass {
    public static void main(String[] args) {
        MaoInjector maoInjector = new MaoInjector(MainClass.class);
        UserDao userDao = maoInjector.getInstance(UserDao.class);
        UserService userService = maoInjector.getInstance(UserService.class);
    }
}
```

**原理&步骤**：
- 1.获取所有@Instance注解的类。这里为了方便使用了Reflections的jar来实现。具体实现在MaoInjector类。
- 2.判断所有@Instance注解的类是否有循环依赖。具体实现在CircularDependencyHelper类。
- 3.开始创建@Instance注解类的实例。分为拿取的是默认的构造器还是带有@Inject注解的构造器。采用递归的方式实现，具体实现在ConstructorInjectHelper类。
- 4.开始构造带有@Instance类中@Provider注解的方法，具体实现在ProviderInjectHelper。
- 5.开始注入有@Instance类中的@Inject注解的属性，具体实现在FieldInjectHelper。
- 具体例子在mao-ioc-example那个module下。