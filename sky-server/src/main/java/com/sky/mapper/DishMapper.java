package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 统计当前分类下有多少个菜品
     * @param id
     * @return
     */
    @Select("select count(*) from dish where dish.category_id = #{id}")
    int getDishCountByCategoryId(Long id);
}
