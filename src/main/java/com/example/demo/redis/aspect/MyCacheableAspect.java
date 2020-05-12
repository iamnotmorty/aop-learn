package com.example.demo.redis.aspect;

import com.example.demo.redis.annotation.MyCacheable;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;


/**
 * Created with IntelliJ IDEA.
 * Author: linjinghui
 * Date: 2020/5/7
 * Time: 17:04
 * Description: No Description
 */
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
//        logger.info("getThis()===>{}", pjp.getThis());
//        logger.info("getTarget()===>{}", pjp.getTarget());
//        logger.info("getArgs()===>{}", pjp.getArgs());
//        logger.info("getKind()===>{}", pjp.getKind());
//        logger.info("getSignature()===>{}", pjp.getSignature());
//        logger.info("getSignature().getDeclaringTypeName()===>{}", pjp.getSignature().getDeclaringTypeName());
//        logger.info("getSignature().getDeclaringType()===>{}", pjp.getSignature().getDeclaringType());
//        logger.info("getSignature().getName()===>{}", pjp.getSignature().getName());
//        logger.info("getSignature().getModifiers()===>{}", pjp.getSignature().getModifiers());
//        logger.info("getSourceLocation()===>{}", pjp.getSourceLocation());
        //logger.info("getSourceLocation()#getFileName===>{}", pjp.getSourceLocation().getFileName());
        //logger.info("getSourceLocation()#getLine===>{}", pjp.getSourceLocation().getLine());
        //logger.info("getSourceLocation()#getWithinType===>{}", pjp.getSourceLocation().getWithinType());

        Object[] objs = pjp.getArgs();
        logger.info("objs =====> {}", objs);
        if (!myCacheable.cacheable()) {
            return pjp.proceed();
        }

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
        //taskExecutor.execute(new CacheFlush(pjp, cache, key));
        logger.info("读取缓存【{}】", wrapper.get());

        return wrapper.get();
    }

    //@Before("@annotation(myCacheable)")
    public void doSomethingBefore(JoinPoint joinPoint, MyCacheable myCacheable) {
        logger.info("@Before：模拟权限检查...");
        logger.info("@Before：目标方法为：" +
                joinPoint.getSignature().getDeclaringTypeName() +
                "." + joinPoint.getSignature().getName());
        logger.info("@Before：参数为：" + Arrays.toString(joinPoint.getArgs()));
        logger.info("@Before：被织入的目标对象为：" + joinPoint.getTarget());
        logger.info("缓存匹配无效，数据库查询");
    }

    //@AfterReturning(pointcut="@annotation(myCacheable)",
    //        returning="returnValue")
    public void log(JoinPoint point, Object returnValue, MyCacheable myCacheable) {
        logger.info("@AfterReturning：模拟日志记录功能...");
        logger.info("@AfterReturning：目标方法为：" +
                point.getSignature().getDeclaringTypeName() +
                "." + point.getSignature().getName());
        logger.info("@AfterReturning：参数为：" +
                Arrays.toString(point.getArgs()));
        logger.info("@AfterReturning：返回值为：" + returnValue);
        logger.info("@AfterReturning：被织入的目标对象为：" + point.getTarget());
        logger.info("缓存成功==>读取缓存...");
    }

    /**
     * 新线程刷新缓存
     */
    private class CacheFlush implements Runnable {
        Cache               cache;
        String              key;
        ProceedingJoinPoint point;

        public CacheFlush(ProceedingJoinPoint point, Cache cache, String key) {
            this.cache = cache;
            this.key = key;
            this.point = point;
        }

        @Override
        public void run() {
            try {
                logger.info("缓存超时 , 更新缓存");
                cache.put(key, point.proceed());
                logger.info("更新缓存成功");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
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
