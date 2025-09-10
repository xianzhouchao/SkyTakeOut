package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/user/shop")
@Slf4j
@Api(tags="店铺相关接口")
public class UserShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/status")
    @ApiOperation("用户端查询店铺状态")
    public Result<Integer> getStatus(){
        Integer status =  Integer.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(KEY))) ;
        log.info("获取到店铺的营业状态为：{}",status == 1 ? "营业中" : "打烊中");
        return Result.success(status) ;
    }
}
