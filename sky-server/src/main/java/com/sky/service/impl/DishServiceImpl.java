package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dishMapper.insert(dish);

        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();

        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);//后绪步骤实现
        }

        String pattern = "dish_" + dishDTO.getCategoryId();
        redisTemplate.delete(pattern);


    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {


        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());

    }

    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        List<Long> afterFilter = new ArrayList<>();
        for(Long id : ids) {

            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == 1 ) throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE); ;


            Boolean ifExistDishId = setmealDishMapper.ifExistDishId(id);
            if (ifExistDishId) throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL); ;

            afterFilter.add(id);

        }

        dishMapper.deleteBatch(afterFilter);

        dishFlavorMapper.deleteBatch(afterFilter);

        String pattern = "dish_*" ;

        redisTemplate.delete(pattern);
    }

    @Override
    public DishVO getById(Long id) {
        Dish dish = dishMapper.getById(id);

        //根据菜品id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);//后绪步骤实现

        //将查询到的数据封装到VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    @Transactional
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {

        Long id = dishDTO.getId();

        List<Long> list = List.of(id) ;
        dishFlavorMapper.deleteBatch(list);


        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dishMapper.update(dish);

        List<DishFlavor> dishFlavors = dishDTO.getFlavors();


        if (dishFlavors != null && dishFlavors.size() > 0) {
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(dishFlavors);
        }

        String pattern  = "dish_" + dishDTO.getCategoryId();
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);

    }

    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();

        return dishMapper.list(dish);
    }

    @Override
    public List<DishVO> listWithFlavor(Long categoryId) {
        List<Dish> dishList = getByCategoryId(categoryId);
        List<DishVO> dishVOList = new ArrayList<>();
        dishList.forEach(dish -> {
            Long dishId = dish.getId();
            List<DishFlavor> dishFlavorsList = dishFlavorMapper.getByDishId(dishId);
            DishVO dishVo = new DishVO();
            BeanUtils.copyProperties(dish, dishVo);
            dishVo.setFlavors(dishFlavorsList);
            if (dishVo.getStatus() == 1 ) dishVOList.add(dishVo);
        });
        return dishVOList;
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.update(dish);
    }
}
