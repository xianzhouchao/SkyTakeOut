package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.service.impl.DishServiceImpl;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags ="C端套餐相关接口")
public class UserSetmealController {


    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishService dishService;

    @ApiOperation("根据分类id查询套餐")
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Long categoryId){
        List<Setmeal> setmealList = setmealService.getSetmealByCategoryId(categoryId);
        return Result.success(setmealList);
    }

    @ApiOperation("根据套餐id查询包含的菜品列表")
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> dishList(@PathVariable Long id){
        List<DishItemVO> list = setmealService.getDishItemById(id);
        return Result.success(list);
    }

}
