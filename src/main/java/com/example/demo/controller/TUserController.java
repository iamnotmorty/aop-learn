package com.example.demo.controller;

import com.example.demo.entity.TUser;
import com.example.demo.service.TUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (TUser)表控制层
 *
 * @author makejava
 * @since 2020-05-07 10:26:43
 */
@RestController
@RequestMapping("tUser")
public class TUserController {
    /**
     * 服务对象
     */
    @Resource
    private TUserService tUserService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public TUser selectOne(Integer id) {
        return this.tUserService.queryById(id);
    }

}