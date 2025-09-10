package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RequestMapping("/admin/shop")
@RestController
@Slf4j
@Api(tags="店铺相关接口")
public class AdminShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PutMapping("/{status}")
    @ApiOperation("设置店铺的营业状态")
    public Result setStatus(@PathVariable("status") Integer status) {
        log.info("设置店铺的营业状态为：{}",status == 1 ? "营业中" : "打烊中" );
        stringRedisTemplate.opsForValue().set(KEY, String.valueOf(status));
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Integer> getStatus() {
        Integer status = Integer.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(KEY))) ;
        log.info("当前店铺营业状态为：{}",status==1? "营业中":"打烊中");
        return Result.success(status);
    }


}
