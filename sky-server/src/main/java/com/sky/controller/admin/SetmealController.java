package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api("套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    @PostMapping
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐{}",setmealDTO);
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> Page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页展示{}",setmealPageQueryDTO);
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @DeleteMapping
    public Result deleteSetmeal(@RequestParam List<Long> ids){
        log.info("删除套餐{}",ids);
        setmealService.deleteSetmeal(ids);
        return Result.success();
    }

    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @PutMapping
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐{}",setmealDTO);
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getSetmealById(@PathVariable Long id){
        log.info("通过id查询套餐{}",id);
        SetmealVO setmealVO = setmealService.getSetmealById(id);
        return Result.success(setmealVO);
    }

    @ApiOperation("套餐起售停售")
    @PostMapping("/{status}")
    public Result updateSetmealStatus(@PathVariable Integer status,@RequestParam Integer id){
        log.info("更新id为{}的套餐的启停状态为{}",id,status);
        setmealService.updateSetmealStatus(status,id);
        return Result.success();
    }
}
