# AOP-Learn
从实践的角度去学习AOP，本项目起始于用AOP实现自定义缓存时的各种思考，由此记录供温习学习使用。
<!-- TOC -->

- [AOP-Learn](#aop-learn)
    - [初始化Springboot项目](#初始化springboot项目)
        - [数据库相关依赖](#数据库相关依赖)
        - [数据库相关配置](#数据库相关配置)
    - [使用Redis实现缓存](#使用redis实现缓存)
        - [添加依赖](#添加依赖)
        - [添加配置](#添加配置)
        - [注解 @EnableCaching](#注解-enablecaching)
        - [注解 @Cacheable、@CacheEvict](#注解-cacheablecacheevict)
    - [自定义缓存注解](#自定义缓存注解)
        - [自定义 @MyCacheable 注解](#自定义-mycacheable-注解)
        - [基于 @Aspect 注解的缓存实现](#基于-aspect-注解的缓存实现)
    - [AOP的深入研究](#aop的深入研究)
        - [AOP相关名词](#aop相关名词)
        - [Spring AOP](#spring-aop)
        - [AspectJ](#aspectj)
    - [代理模式](#代理模式)
        - [静态代理](#静态代理)
        - [动态代理](#动态代理)
            - [JDK动态代理](#jdk动态代理)
            - [CGLib动态代理](#cglib动态代理)

<!-- /TOC -->
## 初始化Springboot项目
初始化这一步 Idea 直接就可以创建了，添加 web ，Mysql ，MyBatis 依赖，
这一步可以直接在创建项目的时候就可以勾选，也可以等到项目创建完成后手动添加。
一般我们还会用到数据库连接池，所以还要添加 druid 依赖。
> 注： 我这里的SpringBoot版本是 2.1.6.RELEASE
### 数据库相关依赖
pom.xml部分内容如下
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>2.1.0</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    <!-- 前面三个依赖在项目创建的时候就可以勾选，druid-spring-boot-starter就需要手动添加。 -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.1.22</version>
    </dependency> 
</dependencies>           
```
我比较喜欢用 MyBatis ，虽然有些人说 JAP 更加面向对象，但是我只能说还是喜欢 MyBatis 的简单。
你用 JAP 写个复杂查询试试，还是很麻烦的。
### 数据库相关配置
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dmj_user
spring.datasource.username=root
spring.datasource.password=123456
```
没错，只要求可用做实验用这个配置就够了，其他的配置按需求添加吧。

MyBatis 的配置只需要设置一个 xml 文件所在的位置就可以了。
```properties
mybatis.mapper-locations=classpath:mapper/*.xml
```
就这么点配置，无需多想，就这样吧。一开始少纠结，先回用，先把项目跑起来。
***
## 使用Redis实现缓存
Redis 相关的东西就不在这里多说了。
至于 MySQL 和 Redis 的版本，个人觉得既然是学习，那就用最新的吧。
我用 Docker Desktop for Windows 来安装 MySQL8.0 和 Redis6.0 。
Docker怎么用这里也不多说了，不是相关的重点。
### 添加依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```
" version "就不需要加了，因为是一个 spring-boot-starter，所以跟随 SpringBoot 的版本就好。

### 添加配置
关于配置这里还想多说几句，去搜索配置相关的文章，一搜一堆，单个人觉得很多都是到处复制来，
说白了很多人跑没跑起来都不知道，直接就把一堆配置抛出来了。个人建议先用最简配置，等需要时
再逐渐添加。就比如前面数据库连接池的配置，简简单单就好，你就跑一个接口，查一张表的信息，
你搞这么多配置晕不晕，关键还整不明白配置的作用，何必呢。
```properties
spring.redis.host=127.0.0.1
spring.redis.password=
spring.redis.port=6379
```
就这么点配置就可以跑起来了，其他的不写会自动使用默认的配置。还是那句话先跑起来，有需要再加。
一般来说官方文档给的配置基本都是最简配置，参照即可，所以官方文档还是要去看。
### 注解 @EnableCaching
@EnableCaching 直接加到 Application 上，放到 @SpringBootApplication 下面就好了。
### 注解 @Cacheable、@CacheEvict
@Cacheable ： 缓存注解
@CacheEvict ： 清除缓存
***
