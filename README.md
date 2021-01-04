# AOP-Learn

2021/1/4 这个项目当时目的是从自定义缓存注解的角度切入 AOP 的理解和学习，目前来看有所偏离轨道，近期会重新整理。
今天开始继续维护github



👉 Just do it. 

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
        - [AOP使用场景](#aop使用场景)
        - [AOP相关名词](#aop相关名词)
        - [AOP实现](#aop实现)
            - [关键的注解](#关键的注解)
            - [增强注解的 pointcut 属性](#增强注解的-pointcut-属性)
        - [Spring AOP](#spring-aop)
        - [AspectJ](#aspectj)
    - [代理模式](#代理模式)
        - [静态代理](#静态代理)
        - [动态代理](#动态代理)
            - [JDK动态代理](#jdk动态代理)
            - [CGLib动态代理](#cglib动态代理)
            - [Aspect动态代理](#aspect动态代理)
            - [Instrumentation动态代理](#instrumentation动态代理)

<!-- /TOC -->
## 初始化Springboot项目
🚀 初始化这一步 Idea 直接就可以创建了，添加 web ，Mysql ，MyBatis 依赖，
这一步可以直接在创建项目的时候就可以勾选，也可以等到项目创建完成后手动添加。
一般我们还会用到数据库连接池，所以还要添加 druid 依赖。
> 注： 我这里的SpringBoot版本是 2.1.6.RELEASE

这里给出连接可以参考，注意 MySQL 和 Redis 都是在 Docker 容器里安装启动的。
[安装Docker](https://www.runoob.com/docker/windows-docker-install.html) | [安装MySQL](https://www.runoob.com/docker/docker-install-mysql.html) | [安装Redis](https://www.runoob.com/docker/docker-install-redis.html)  
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
这里用 MyBatis ，当然其实可以用 JPA ，因为这里涉及的东西很简单，反倒用 JPA 可以少一些东西，看个人选择吧。
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
就这么点配置，无需多想，就这样吧。一开始少纠结，先会用，先把项目跑起来。
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
## 自定义缓存注解
Spring 提供的 @Cacheable 所提供的功能虽然已经有很多功能了，但是举一个最简单的例子，
我需要自定义缓存过期时间。这样 @Cacheable 就没办法提供给你了。由此我们可以自定义一个
注解，满足自己的业务需求。
### 自定义 @MyCacheable 注解
创建一个 @MyCacheable , 目前我们先简单的定义一个。满足最基本的需求，缓存。
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyCacheable {
    String value() default "";

    String key() default "";
}
```
没错，只需要这两个就可以了，实现基本的缓存我们只需要 value 和 key。
### 基于 @Aspect 注解的缓存实现
啥？你问我配置文件的方式怎么实现。不好意思，懒得写也不会，都2020年了，
springboot 的理念就是约定大于配置，引入包。一个 @Aspect 开干就完了。

好了，开个玩笑，这里的目的就是快速实现自定义缓存注解，赶紧先动起来，成功实现了再说。
至于 Spring AOP 后面内容会讲，这么讲的原因还是一个理念，先实践，再刨根问底。
创建一个 Aspect 来处理 @MyCacheable 标记的业务处理类的某个方法。
```java
@Component
@Aspect
public class MyCacheableAspect {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CacheManager cacheManager;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Around("@annotation(myCacheable)")
    public Object myCacheAround(ProceedingJoinPoint pjp, MyCacheable myCacheable) throws Throwable {
        String  key = parseSpelToString(pjp,myCacheable.key());
        String cacheName = myCacheable.value();

        Cache cache = cacheManager.getCache(cacheName);
        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper == null) {
            logger.info("缓存匹配无效，数据库查询");
            cache.put(key, pjp.proceed());
            logger.info("缓存成功==>读取缓存...");
            return cache.get(key).get();
        }

        logger.info("命中缓存：" + wrapper.get());
        logger.info("读取缓存【{}】", wrapper.get());

        return wrapper.get();
    }

    /**
     * SpEl 表达式解释成字符串
     * @param point 切入对象
     * @param spel spel 表达式
     * @return
     */
    public String parseSpelToString(ProceedingJoinPoint point,String spel){
        if(spel != null){
            LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            EvaluationContext context = new StandardEvaluationContext();
            String[] params = discoverer.getParameterNames(method);
            Object[] args = point.getArgs();
            for (int len = 0; len < params.length; len++) {
                context.setVariable(params[len], args[len]);
            }

            SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
            SpelExpression spelExpression = spelExpressionParser.parseRaw(spel);
            return spelExpression.getValue(context,String.class);
        }
        return "";
    }
}
```
这个类以上的代码已经是很简单了，直接就能用的。两个方法：
1. MyCacheableAspect#myCacheableAround() 注解 @MyCacheable 标记的方法的环绕增强，
用于处理获取缓存和插入缓存逻辑。
2. MyCacheableAspect#parseSpelToString() 该方法用于解析 spel 表达式为字符串。用来
将设定的 例如 key = "#id" 解析成传入 id 字符串。这个方法没什么难懂的，就是几个类，
作用都很明显。