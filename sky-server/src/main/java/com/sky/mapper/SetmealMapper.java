package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    @Insert("INSERT INTO setmeal " +
            "(category_id, name, price, description, image, status, create_time, update_time, create_user, update_user) " +
            "VALUES " +
            "(#{categoryId}, #{name}, #{price}, #{description}, #{image}, #{status}, " +
            "#{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void addSetMeal(Setmeal setmeal);

    Page<Setmeal> page();

    List<Setmeal> getByIds(List<Long> ids);

    void deleteByIds(List<Long> ids);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getSetmealById(Long id);

    @AutoFill(OperationType.UPDATE)
    void updateSetMeal(Setmeal setmeal);
}
