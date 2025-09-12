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
    private DishService dishService;

    @GetMapping("/list")
    @ApiOperation("展示分类下的套餐列表")
    public Result<List<DishVO>> listDishes (@RequestParam Long categoryId) {


        List<DishVO> dishVOList = dishService.listWithFlavor(categoryId);

        return Result.success(dishVOList);

    }


}
