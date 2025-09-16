package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Transactional
    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.addSetMeal(setmeal);

        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes() ;
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        for(SetmealDish setmealDish : setmealDishes){
            setmealDishMapper.addSetmealDish(setmealDish);
        }

    }

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Integer categoryId = setmealPageQueryDTO.getCategoryId();
        Integer status = setmealPageQueryDTO.getStatus();
        String name = setmealPageQueryDTO.getName();

        Page<SetmealVO> page =  setmealMapper.page(categoryId,status,name);

        return new PageResult(page.getTotal(),page.getResult());

    }

    @Override
    public void deleteSetmeal(List<Long> ids) {
        List<Setmeal> list = setmealMapper.getByIds(ids);
        for(Setmeal setmeal : list){
            if ( setmeal.getStatus() == 1 ) throw new DeletionNotAllowedException("在售套餐无法删除");
        }

        setmealMapper.deleteByIds(ids);
        setmealDishMapper.deleteBySetmealIds(ids);
    }

    @Transactional
    @Override
    public void updateSetmeal(SetmealDTO setmealDTO) {
        List<SetmealDish> setmealDishLists = setmealDTO.getSetmealDishes();
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.updateSetmeal(setmeal);

        Long setmealId = setmeal.getId();

        setmealDishMapper.deleteBySetmealIds(List.of(setmealId));

        setmealDishLists.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        for(SetmealDish setmealDish : setmealDishLists){
            setmealDishMapper.addSetmealDish(setmealDish);
        }

    }

    @Override
    public SetmealVO getSetmealById(Long id) {
        Setmeal setmeal = setmealMapper.getSetmealById(id) ;
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);

        String categoryName = categoryMapper.getCategoryNameById(setmeal.getCategoryId());

        List<SetmealDish> listSetmealDish = setmealDishMapper.getBySetmealId(setmeal.getId());

        setmealVO.setCategoryName(categoryName);
        setmealVO.setSetmealDishes(listSetmealDish);

        return setmealVO ;
    }

    @Override
    public void updateSetmealStatus(Integer status, Integer id) {
        if (status == StatusConstant.ENABLE){
            List<SetmealDish> setmealDishList = setmealDishMapper.getBySetmealId(id.longValue());
            List<Long> dishNumList = new ArrayList<>();
            setmealDishList.forEach(setmealDish -> {
                dishNumList.add(setmealDish.getDishId());
            });

            for (Long num : dishNumList) {
                Dish dish = dishMapper.getById(num);
                if (dish.getStatus() == StatusConstant.DISABLE){
                    throw new SetmealEnableFailedException();
                }
            }

            Setmeal setmeal = Setmeal.builder()
                    .id(id.longValue())
                    .status(status)
                    .build();

            setmealMapper.updateSetmeal(setmeal);
        }
    }

    @Override
    public List<Setmeal> getSetmealByCategoryId(Long categoryId) {

        List<Setmeal> settmealList = setmealMapper.getSetmealByCategoryId(categoryId);
        return settmealList;
    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

}
