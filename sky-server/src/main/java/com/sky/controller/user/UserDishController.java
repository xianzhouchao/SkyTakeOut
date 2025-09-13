package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user/dish")
@Api( tags = "C端菜品相关接口")
@Slf4j
public class UserDishController {


    @Autowired
    private RedisTemplate redisTemplate ;

    @Autowired
    private DishService dishService;

    @GetMapping("/list")
    @ApiOperation("展示分类下的菜品列表")
    public Result<List<DishVO>> listDishes (@RequestParam Long categoryId) {

        String key = "dish_" + categoryId;

        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (list != null && list.size() > 0) {
            return Result.success(list);
        }


        List<DishVO> dishVOList = dishService.listWithFlavor(categoryId);

        redisTemplate.opsForValue().set(key,dishVOList);

        return Result.success(dishVOList);

    }


}
