package com.example.demo.cao;

import com.example.demo.dao.TUserDao;
import com.example.demo.entity.TUser;
import com.example.demo.redis.annotation.MyCacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Author: linjinghui
 * Date: 2020/5/7
 * Time: 10:59
 * Description: No Description
 */
@Component("tUserCao")
public class TUserCao {

    private Logger logger = LoggerFactory.getLogger(TUserCao.class);

    @Resource
    private TUserDao tUserDao;

    @MyCacheable(value = "DMJUser:tUser", key = "#id", cacheable = true)
    //@Cacheable(value = "DMJUser:tUser", key = "#id")
    public TUser queryById(Integer id) {
        logger.info("==>从数据库获取ID【" + id + "】的用户");
        TUser tUser = tUserDao.queryById(id);
        logger.info("==>从数据库获取ID【" + id + "】的用户成功");
        return tUser;
    }
}
