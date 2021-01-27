## 深入 AOP

深入学习技术的原理的时候我们都要问自己三个问题：

1.是什么？

2.干什么用？

3.怎么实现（原理是什么）？

基本是从这个流程入手就可以整理清楚

### AOP是什么

> 在软件业，AOP 为 Aspect Oriented Programming 的缩写，意为：面向切面编程，
> 通过预编译方式和运行期间动态代理实现程序功能的统一维护的一种技术。AOP 是 OOP
> 的延续，是软件开发中的一个热点，也是 Spring 框架中的一个重要内容，是函数式编程
> 的一种衍生范型。利用 AOP 可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各
> 部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。

### AOP使用场景

Authentication（权限）、Caching（缓存）、Context passing（内容传递）、Error handling（错误处理）、
Lazy loading（懒加载）、Debugging（调试）、logging（记录）、tracing（跟踪）、profiling（优化）、
monitoring（校准）、Performance optimization（性能优化）、Persistence（持久化）、Resource pooling（资源池）、
Synchronization（同步）、Transactions（事务）

前面我们有已经实现了简单的缓存，以后还会继续拓展。

### AOP相关名词

- JoinPoint 连接点

> a point during the execution of a program, such as the execution of a method or the handling of an exception. 
> In Spring AOP, a join point always represents a method execution.

> 程序运行中的一些时间点, 例如一个方法的执行, 或者是一个异常的处理.
> 在 Spring AOP 中, join point 总是表示方法的执行. 

Java程序可以看成是一个个方法的调用链。在 AOP 的概念里，调用链的每一个方法就是一个
连接点(join point) ，因此就可以把 Java程序看成是由一个个连接点和一根垂直的时间轴组成的链条。

- Aspect 切面

Aspect 切面我们可以理解为，指定某个地方要添加的操作（即包含 Pointcut 的定义和 Advice 的定义）。从空间角度来说就是横
切于连接点链条。所以叫做切面。我们代码中会用到 "@Aspect" 注解，也就认为用 @Aspect 标注的类就是一个切面类。

- Pointcut 切点

切面切入的位置就被叫做切点。

从代码层面来说， pointcut 的作用就是提供一组规则(使用 AspectJ pointcut expression language 来描述) 来匹配 join point, 
给满足规则的 join point 添加 Advice.

`这里再多说两句，join point 和 pointcut 的区别就是，前者是目标，后者是指定目标的规则。`

- Advice 增强

由 Aspect 添加到特定的 join point (即满足 pointcut 匹配规则的 join point ) 的一段代码.
可以在切点位置通过切面对目标方法进行增强处理，增强处理有几种如下：

- Before Advice 前置增强

方法调用前

- After Advice 后置增强

方法调用后

- Around Advice 环绕增强

环绕方法，在方法调用前后

- Throws Advice 抛出增强

抛出异常后

- Introduction Advice 引入增强
对某一个类进行增强

- Weaving 织入（对方法的增强）

编译时织入，运行时织入

- Introduction 引入（对类的增强）

一个类没有实现某接口，想要该类具备某接口的方法。也就可以用引入增强来实现。
实现引入增强可以将 B b = (B)a;
可以将没有实现B接口的A类实例强转为B类型;
即可称为“接口动态实现”


### AOP实现

现在有了 SpringBoot 这个神器， 真的越来越简单了。不用像以前那样配置来配置去的，也不用加那么多包，
直接一个 spring-boot-starter-aop 就搞定。从实现代码层面思考一下，为什么说到 AOP 就会扯到代理
模式，用很简单的方式来说，我们代理目标类，用代理类在原来类基础实现我们需要的处理。然后调用代理类，
就可以实现对原有方法的增强。

#### 关键的注解

@Aspect：标注这个类是一个切面

@Pointcut：定义切点

@Around：环绕增强

@Before：在切入点方法执行之前执行我们定义的advice。

@After：在切入点方法执行之后执行我们定义的advice

@AfterReturning：目标方法完成后织入，比起 @After 还多一个 returning 属性，目标方法的返回对象。

@AfterThrowing：异常抛出增强，在异常抛出后织入的增强。有点像上面的@AfterReturning，这个注解也是有两个属性，pointcut和throwing。

#### 增强注解的 pointcut 属性

- pointcut 属性可以这么多定义方式，前面在自定义注解的时候用到了 @annotation ，也就是我们使用注解的地方。

- execution: 匹配连接点

- within: 某个类里面

- this: 指定AOP代理类的类型

- target:指定目标对象的类型

- args: 指定参数的类型

- bean:指定特定的bean名称，可以使用通配符（Spring自带的）

- @target： 带有指定注解的类型

- @args: 指定运行时传的参数带有指定的注解

- @within: 匹配使用指定注解的类

- @annotation:指定方法所应用的注解

我们重点讲一下 execution ，这个相对用的比较多。

> execution(方法修饰符（可选） 返回类型（必须） 类路径（可选） 方法名（必须） 参数（必须） 异常模式（可选）)

例如：execution(* com.example.demo.cao.TUserCao.queryById(*))

            返回类型        类路径                方法名   参数

> 注：要知道参数的 (\*) 表示一个参数，其他出现 * ，表示所有。"com.example.demo.cao..*." 一般用在类路径，表示cao包和它的子包下的方法

## Spring AOP

织入 advice 的目标对象. 目标对象也被称为 advised object.
Spring AOP 使用运行时代理的方式来实现 aspect, 因此 adviced object 总是一个代理对象(proxied object)。

***

## 代理模式

代理类代理委托类，客户端不直接调用委托类的目标方法，而是通过代理类间接的调用目标方法。
这种模式增加了客户端调用目标方法的间接性，正是这种间接性提供了在目标方法调用前后增加处理逻辑的空间。

> 注: 后续会用到Target（目标/委托类），Proxy（代理）

### 静态代理

代理类和委托类需要实现同一个接口（必须），代理类持有委托类的引用，在代理类调用目标方法时可以添加
增强处理（相当与手动织入），在客户端表现为，调用代理类的委托类的同名方法。直接代码层面的编写，局限性大。

> 实现比较简单。代码如下

```java
//接口
public interface Target {
    void doSomething();
}
//实现类（目标类）
public class TargetImpl implements Target {
    @Override
    public void doSomething() {
        System.out.println("target do something!!!");
    }
}
//代理类
public class TargetProxy implements Target {
    private Target baseObject;

    public TargetProxy(Target baseObject) {
        this.baseObject = baseObject;
    }

    @Override
    public void doSomething(){
        System.out.println("before method");
        baseObject.doSomething();
        System.out.println("after method");
    }
}

//测试
public class TargetMain {
    public static void main(String[] args) {
        executeMethod();
    }

    public static void executeMethod() {
        Target target =  new TargetImpl();
        TargetProxy targetProxy = new TargetProxy(target);
        targetProxy.doSomething();
    }
}
```

刚入门的同学可能觉得不好理解，没关系，其实到后面看起来真的很简单，甚至会觉得这种方式很“弱智”。

### 动态代理

说白了就是使用语言特性，改善静态代理需要手动写入代码的不足之处。动态的实现代理，使得拓展更加方便灵活，
同时在代码的层面降低浸入性(运行时或者编译时织入），代码职责更加清晰。

#### JDK动态代理

> 利用Java反射机制在运行时创建代理类

 Talk is cheap, show you my code.

```java
/**
 * 目标接口
 */
public interface Person {
    /**
     *
     * @param name
     * @param dst
     */
    void goWorking(String name, String dst);
    /**
     * 获取名称
     * @return
     */
    String getName( );
    /**
     * 设置名称
     * @param name
     */
    void  setName(String name);
}

/**
 * 目标类
 */
public class SoftwareEngineer implements Person {

    private String name;

    public SoftwareEngineer(String name) {
        this.name = name;
    }

    @Override
    public void goWorking(String name, String dst) {
        System.out.println("name ="+name+" ， 去 "+dst +" 工作");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}

/**
 * invocation handler
 */
public class MonitorTargetInvocationHandler<T> implements InvocationHandler {
    // 这里用泛型类或者直接使用Object都可以
    T target;

    public MonitorTargetInvocationHandler(T target) {
        this.target = target;
    }

    /**
     * 在
     * @param proxy  代表动态生成的 动态代理 对象实例
     * @param method 代表被调用委托类的接口方法，和生成的代理类实例调用的接口方法是一致的，它对应的Method 实例
     * @param args   代表调用接口方法对应的Object参数数组，如果接口是无参，则为null； 对于原始数据类型返回的他的包装类型。
     * @return 返回调用结果 
     * @throws Throwable 抛出异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 在转调具体目标对象之前，可以执行一些功能处理
        System.out.println("被动态代理类回调执行, 代理类 proxyClass ="+proxy.getClass()+" 方法名: " + method.getName() + "方法. 方法返回类型："+method.getReturnType()
                +" 接口方法入参数组: "+(args ==null ? "null" : Arrays.toString(args)));
        // 计算该方法耗时 BEGIN
        // 开始时间
        long startTime = System.currentTimeMillis();
        // 调用被代理对象的真实方法
        Object result = method.invoke(target, args);
        //结束时间
        long finishTime = System.currentTimeMillis();
        // 计算该方法耗时 END
        System.out.println(method.getName() + "方法执行耗时" + (finishTime - startTime + "ms");
        return result;
    }

    public static Object getProxy(Class<?> interfaceClazz, Object target) {
        return Proxy.newProxyInstance(interfaceClazz.getClassLoader(), new Class<?>[]{interfaceClazz},
                new MonitorTargetInvocationHandler<>(target));
    }
}

/**
 * 测试类
 */
public class JDKDynamicProxyTest {
    public static void main(String[] args) {
        Person person = new SoftwareEngineer("Vincent");
        Person personProxy = (Person) MonitorTargetInvocationHandler.getProxy(Person.class, person);
        System.out.println("package = " + personProxy.getClass().getPackage() + " SimpleName = " + personProxy.getClass().getSimpleName() + " name =" + personProxy.getClass().getName() + " CanonicalName = " + "" + personProxy.getClass().getCanonicalName() + " 实现的接口 Interfaces = " + Arrays.toString(personProxy.getClass().getInterfaces()) + " superClass = " + personProxy.getClass().getSuperclass() + " methods =" + Arrays.toString(personProxy.getClass().getMethods()));
        // 通过 代理类 执行 委托类的代码逻辑
        String name = personProxy.getName();
        personProxy.goWorking(name, "深圳");
    }
}

```

下面来看分析

可以看到，我们创建的 Handler 实现了 InvocationHandler 接口并实现了 invoke() 方法。在JDK动态代理中，核心是
InvocationHandler。每一个代理的实例都会有一个关联的调用处理程序(InvocationHandler) ，这个处理程序关联了被代理
对象。对待代理实例进行调用时，将对方法的调用进行编码并指派到它的调用处理器(InvocationHandler)的invoke方法。所以
对代理对象实例方法的调用都是通过InvocationHandler中的invoke方法来完成的，而invoke方法会根据传入的代理对象、方法
名称以及参数决定调用代理的哪个方法。

在测试类里调用了 MonitorTargetInvocationHandler.getProxy(Class<?> interfaceClazz, Object target) 这个静态方
法获取代理类。这个方法里封装了 Proxy.newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h) 
从方法名可以很直观的看出，创建一个代理实例。接下来我们来读读这个方法的源码。

```java
public class Proxy implements java.io.Serializable {
    /**
     * Returns an instance of a proxy class for the specified interfaces
     * that dispatches method invocations to the specified invocation
     * handler.
     * 返回指定接口的代理类实例，它会将方法调用分派到指定的调用处理(InvocationHandler)
     *
     * <p>{@code Proxy.newProxyInstance} throws
     * {@code IllegalArgumentException} for the same reasons that
     * {@code Proxy.getProxyClass} does.
     *
     * @param   loader the class loader to define the proxy class 限定代理类的类加载器
     * @param   interfaces the list of interfaces for the proxy class
     *          to implement 代理类要实现的接口数组
     * @param   h the invocation handler to dispatch method invocations to 调用处理，将代理类的方法调用分派给他
     * @return  a proxy instance with the specified invocation handler of a
     *          proxy class that is defined by the specified class loader
     *          and that implements the specified interfaces
     * @throws  IllegalArgumentException if any of the restrictions on the
     *          parameters that may be passed to {@code getProxyClass}
     *          are violated
     * @throws  SecurityException if a security manager, <em>s</em>, is present
     *          and any of the following conditions is met:
     *          <ul>
     *          <li> the given {@code loader} is {@code null} and
     *               the caller's class loader is not {@code null} and the
     *               invocation of {@link SecurityManager#checkPermission
     *               s.checkPermission} with
     *               {@code RuntimePermission("getClassLoader")} permission
     *               denies access;</li>
     *          <li> for each proxy interface, {@code intf},
     *               the caller's class loader is not the same as or an
     *               ancestor of the class loader for {@code intf} and
     *               invocation of {@link SecurityManager#checkPackageAccess
     *               s.checkPackageAccess()} denies access to {@code intf};</li>
     *          <li> any of the given proxy interfaces is non-public and the
     *               caller class is not in the same {@linkplain Package runtime package}
     *               as the non-public interface and the invocation of
     *               {@link SecurityManager#checkPermission s.checkPermission} with
     *               {@code ReflectPermission("newProxyInPackage.{package name}")}
     *               permission denies access.</li>
     *          </ul>
     * @throws  NullPointerException if the {@code interfaces} array
     *          argument or any of its elements are {@code null}, or
     *          if the invocation handler, {@code h}, is
     *          {@code null}
     */
    @CallerSensitive
    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
        throws IllegalArgumentException
    {
        // 校验 h 是否为 null，是则抛出 NPE
        Objects.requireNonNull(h);
        
        // 复制一份 interfaces 声明为 final
        final Class<?>[] intfs = interfaces.clone();
        // 获取 Java安全管理器
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            // Check permissions required to create a Proxy class.
            // 检查创建代理类所需的权限
            checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
        }

        /*
         * Look up or generate the designated proxy class.
         * 查找或者生成指定的代理类
         */
        Class<?> cl = getProxyClass0(loader, intfs);

        /*
         * Invoke its constructor with the designated invocation handler.
         * 调用构造器生成指定的应用处理类
         */
        try {
            if (sm != null) {
                // 检查创建代理类所需的权限
                checkNewProxyPermission(Reflection.getCallerClass(), cl);
            }

            final Constructor<?> cons = cl.getConstructor(constructorParams);
            final InvocationHandler ih = h;
            // 若类标识符不为 public 则返回空
            if (!Modifier.isPublic(cl.getModifiers())) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    public Void run() {
                        cons.setAccessible(true);
                        return null;
                    }
                });
            }
            // 返回一个包含指定处理程序数组的实例
            return cons.newInstance(new Object[]{h});
        // 抛出一些异常
        } catch (IllegalAccessException|InstantiationException e) {
            throw new InternalError(e.toString(), e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new InternalError(t.toString(), t);
            }
        } catch (NoSuchMethodException e) {
            throw new InternalError(e.toString(), e);
        }
    }
}
```



#### CGLib动态代理

用 CGLib 实现动态代理的核心就是实现 MethodInterceptor 重写 intercept ，解释分析在代码注释里面有了。
可以看到的是，这种方式可以不需要强制目标类实现一个接口，也不需要在使用的时候显式的初始化一个目标类
实例，只需传入目标类的class对象即可。然后直接生成一个类型为目标类的代理类。

```java
/**
 * 目标类/被代理类
 */
public class Dog {
    public String  call() {
        System.out.println("wang wang wang");
        return "Dog ..";
    }
}

/**
 * 方法拦截器
 */
public class CGLibMethodInterceptor implements MethodInterceptor {
    /**
     * 用于生成 Cglib 动态代理类工具方法
     * @param target 代表需要 被代理的 委托类的 Class 对象
     * @return
     */
    public Object cglibProxyGenerate(Class<?> target) {
        /* 创建cglib 代理类 start */
        // 创建加强器，用来创建动态代理类
        Enhancer enhancer = new Enhancer();
        // 为代理类指定需要代理的类，也即是父类
        enhancer.setSuperclass(target);
        // 设置方法拦截器回调引用，对于代理类上所有方法的调用，都会调用CallBack，而Callback则需要实现intercept() 方法进行拦截
        enhancer.setCallback(this);
        // 获取动态代理类对象并返回
        return enhancer.create();
        /* 创建cglib 代理类 end */
    }


    /**
     * 功能主要是在调用业务类方法之前 之后添加统计时间的方法逻辑.
     * intercept 因为  具有 MethodProxy proxy 参数的原因 不再需要代理类的引用对象了,直接通过proxy 对象访问被代理对象的方法(这种方式更快)。
     * 当然 也可以通过反射机制，通过 method 引用实例    Object result = method.invoke(target, args); 形式反射调用被代理类方法，
     * target 实例代表被代理类对象引用, 初始化 CglibMethodInterceptor 时候被赋值 。但是Cglib不推荐使用这种方式
     * @param o    代表Cglib 生成的动态代理类 对象本身
     * @param method 代理类中被拦截的接口方法 Method 实例
     * @param objects   接口方法参数
     * @param methodProxy  用于调用父类真正的业务类方法。可以直接调用被代理类接口方法
     * @return Object
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("before");
        long startTime = System.currentTimeMillis();
        // 调用被代理对象的真实方法
        Object result = methodProxy.invokeSuper(o, objects);
        // 结束时间
        long finishTime = System.currentTimeMillis();
        System.out.println("after");
        System.out.println(method.getName() + "方法执行耗时" + (finishTime - startTime + "ms");
        return result;
    }
}

/**
 * 测试类
 */
public class CGLibProxyTest {
    public static void main(String[] args) {
        CGLibMethodInterceptor cgLibMethodInterceptor = new CGLibMethodInterceptor();
        Dog dog = (Dog) cgLibMethodInterceptor.cglibProxyGenerate(Dog.class);
        System.out.println(dog.call());
    }
}
```


#### AspectJ动态代理
基于 Aspectj 实现动态代理（修改目标类的字节，织入代理的字节，在程序编译的时候 插入动态代理的字节码，
不会生成全新的Class ）

#### Instrumentation动态代理
基于instrumentation实现动态代理。修改目标类的字节码、类加载的时候动态拦截去修改，
基于javaagent实现-javaagent:spring-instrument-4.3.8.RELEASE.jar，类加载的时候插入动态代理的字节码，
不会生成全新的Class文件。