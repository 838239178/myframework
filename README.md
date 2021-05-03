# Myframework

> :star:**右侧Package可查看Maven坐标并下载Jar包**
>
> :mortar_board:[​实战项目，点击跳转](https://github.com/838239178/assgin5)

## 使用背景

框架适用于Tomcat开发的JavaWeb项目，目前仅适用于配合JSP使用。该框架分离用户逻辑和基本逻辑，让使用者专注于主要业务逻辑的开发，但同时也提供了部分自定义功能。

*框架灵感来自Spring和Mybatis，主要思想于其相似。*

（框架目前处于实验初期，功能较少，代码质量有待改善）

## 功能说明

### 依赖注入

框架能够对`Controller`所需的组件进行依赖注入。

*使用注解：@Autowired @Component*

### 控制层映射

框架对`Servlet`进行封装，约定前缀为`/api/*`的请求将自动映射到`Controller`中执行并返回`ModelAndView	`作为结果，对控制层参数能够自动封装并注入。

*使用注解：@Mapping @Component @Param*

### 持久层模板

框架封装了`JDBC`接口，仅需使用`@Autowired`注入`JdbcTemplate`即可使用。

#### JdbcTemplate

使用方法类似于Spring的JdbcTemplate，但实现了类似于Mybatis的参数映射，即使用`#{}`作为占位符。

提供如下六个接口：

```java
    /**
     * 执行更新操作
     *
     * @param sql  使用<B>?</B>作为占位符！！
     * @param args 参数
     * @return effect rows
     */
    public int executeUpdate(String sql, Object... args) {}

    /**
     * 执行更新操作
     *
     * @param sql  使用<B>#{prop}</B>作为占位符！！
     * @param bean java bean
     * @return effect rows
     */
    public int executeUpdate(Object bean, String sql) {}

    /**
     * 执行查询操作，返回单个对象
     *
     * @param sql  使用<B>#{prop}</B>作为占位符！！
     * @param bean java bean
     * @return T
     */
    @SuppressWarnings("unchecked")
    public <T> T queryObject(String sql, Object bean, ResultMap resultMap)

    /**
     * 执行查询操作，返回单个对象
     *
     * @param sql  使用<B>?</B>作为占位符！！
     * @param args 参数
     * @return T
     */
    @SuppressWarnings("unchecked")
    public <T> T queryObject(String sql, ResultMap resultMap, Object... args)

    /**
     * 执行查询操作，返回多个对象的结果集
     *
     * @param sql  使用<B>?</B>作为占位符！！
     * @param args 参数
     * @return {@link List}
     */
    public <T> List<T> queryList(String sql, ResultMap resultMap, Object... args)

    /**
     * 执行查询操作，返回多个对象的结果集
     *
     * @param sql  使用<B>#{prop}</B>作为占位符！！
     * @param bean java bean
     * @return {@link List}
     */
    public <T> List<T> queryList(String sql, Object bean, ResultMap resultMap)
```

*目前不支持返回自增长主键*

### 拦截器

对使用了注解的控制层方法，框架能够找到对应的拦截器进行拦截。

拦截分为：前置拦截（preHandle）和后置拦截（afterHandle）

可自定义拦截器，需实现接口`Interceptor`并进行配置。

*使用注解：@DoInterceptor*

## 配置文件

框架依赖于配置文件，且必须存在名为`myframework.yml`

配置文件完整结构：

```yml
controller: []
interceptor: []
jdbcConfig:
  driver:
  url:
  username:
  password:
```

## 注解说明

| 注解           | 使用场景                     | 作用                                    |
| -------------- | ---------------------------- | --------------------------------------- |
| @Autowired     | 需要注入的`@Component`类成员 | 依赖注入                                |
| @Component     | 注解在类上                   | 加入Bean容器                            |
| @Mapping       | 控制层方法上或类上           | 自动映射路径到方法                      |
| @Param         | 控制层方法参数上             | 匹配请求中的参数，当参数大于2时必须使用 |
| @DoInterceptor | 控制层方法上                 | 匹配一个拦截器                          |

## 接口/抽象父类说明

- Controller

  控制层公共的父类，每个控制层类都需要继承该类

- Dao

  持久层公共父类，包含获取ResultMap的方法，必须继承此类。并实现其`resultMap`方法。

  - ResultMap

    对`Map`进行封装，返回Dao所需的对象。

- Interceptor

  拦截器接口，实现其`preHandle`、`afterHandle`对方法进行拦截，拦截器名为`@Component`中的参数值，默认为类名，实现`support`方法对限定方法进行筛选。

