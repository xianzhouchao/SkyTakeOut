package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    @Select("select exists (select 1 from setmeal_dish s where s.dish_id = #{id} )")
    Boolean ifExistDishId(Long id);

    @Insert("insert into setmeal_dish ( setmeal_id, dish_id, name, price, copies) VALUES " +
            "(#{setmealId}, #{dishId}, #{name}, #{price}, #{copies})")
    void addSetmealDish(SetmealDish setmealDish);

    void deleteBySetmealIds(List<Long> ids);

    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

}
