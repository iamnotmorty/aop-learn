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
***
## AOP的深入研究
> 在软件业，AOP为Aspect Oriented Programming的缩写，意为：面向切面编程，
>通过预编译方式和运行期间动态代理实现程序功能的统一维护的一种技术。AOP是OOP
>的延续，是软件开发中的一个热点，也是Spring框架中的一个重要内容，是函数式编程
>的一种衍生范型。利用AOP可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各
>部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。———— 百度百科
### AOP使用场景
Authentication（权限）、Caching（缓存）、Context passing（内容传递）、Error handling（错误处理）、
Lazy loading（懒加载）、Debugging（调试）、logging（记录）、tracing（跟踪）、profiling（优化）、
monitoring（校准）、Performance optimization（性能优化）、Persistence（持久化）、Resource pooling（资源池）、
Synchronization（同步）、Transactions（事务）
### AOP相关名词
JoinPoint 连接点

Pointcut 切点

Advice 增强

Adviser 切面

Weaving 织入

Introduction 引入

Before Advice 前置增强

After Advice 后置增强

Around Advice 环绕增强

Throws Advice 抛出增强

Introduction Advice 引入增强
### AOP实现
现在有了 SpringBoot 这个神器， 真的越来越简单了。不用像以前那样配置来配置去的，也不用加那么多包，
直接一个 spring-boot-starter-aop 就搞定。
#### 关键的注解
@Aspect：标注这个类是一个切面

@Pointcut：定义切点

@Around：环绕增强

@Before：在切入点方法执行之前执行我们定义的advice。

@After：在切入点方法执行之后执行我们定义的advice

@AfterReturning：目标方法完成后织入，比起 @After 还多一个 returning 属性，目标方法的返回对象。

@AfterThrowing：异常抛出增强，在异常抛出后织入的增强。有点像上面的@AfterReturning，这个注解也是有两个属性，pointcut和throwing。
#### 增强注解的 pointcut 属性
pointcut 属性可以这么多定义方式，前面在自定义注解的时候用到了 @annotation ，也就是我们使用注解的地方。

execution: 匹配连接点

within: 某个类里面

this: 指定AOP代理类的类型

target:指定目标对象的类型

args: 指定参数的类型

bean:指定特定的bean名称，可以使用通配符（Spring自带的）

@target： 带有指定注解的类型

@args: 指定运行时传的参数带有指定的注解

@within: 匹配使用指定注解的类

@annotation:指定方法所应用的注解

我们重点讲一下 execution ，这个相对用的比较多。
> execution(方法修饰符（可选） 返回类型（必须） 类路径（可选） 方法名（必须） 参数（必须） 异常模式（可选）)

例如：execution(* com.example.demo.cao.TUserCao.queryById(*))

            返回类型        类路径                方法名   参数

> 注：要知道处理参数的一个*，其他出现 * ，表示所有。"com.example.demo.cao..*." 一般用在类路径，表示cao包和它的子包下的方法
### Spring AOP
### AspectJ
***
## 代理模式
代理类代理委托类，客户端不直接调用委托类的目标方法，而是通过代理类间接的调用目标方法。
这种模式增加了客户端调用目标方法的间接性，正是这种间接性提供了在目标方法调用前后增加处理逻辑的空间。
> 注: 后续会用到Target（目标/委托类），Proxy（代理）
### 静态代理
代理类和委托类需要实现同一个接口（必须），代理类持有委托类的引用，在代理类调用目标方法时可以添加
增强处理（相当与手动织入），在客户端表现为，调用代理类的委托类的同名方法。直接代码层面的编写，局限性大。
### 动态代理
#### JDK动态代理
#### CGLib动态代理
#### Aspect动态代理
#### Instrumentation动态代理