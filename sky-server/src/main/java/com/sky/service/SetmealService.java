package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;


public interface SetmealService {

    void addSetmeal(SetmealDTO setmealDTO);

    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteSetmeal(List<Long> ids);

    void updateSetmeal(SetmealDTO setmealDTO);

    SetmealVO getSetmealById(Long id);

    void updateSetmealStatus(Integer status, Integer id);

    List<Setmeal> getSetmealByCategoryId(Long categoryId);

    List<DishItemVO> getDishItemById(Long id);
}
