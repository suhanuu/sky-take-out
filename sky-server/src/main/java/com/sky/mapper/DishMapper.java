package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 统计当前分类下有多少个菜品
     *
     * @param id
     * @return
     */

    @Select("select count(*) from dish where dish.category_id = #{id}")
    int getDishCountByCategoryId(Long id);

    /**
     * 新增菜品数据
     *
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insertDish(Dish dish);

    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 修改菜品数据
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)

    void update(Dish dish);

    /**
     * 根据id查询菜品数据
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    DishVO searchById(Long id);

    /**
     * 跟据分类id查询菜品
     * @param categoryId
     * @return
     */

    @Select("select * from dish where category_id = #{categoryId}")
    List<DishVO> queryByCategoryId(Long categoryId);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 跟据分类id查询菜品
     * @param dish
     * @return
     */
    List<DishVO> getDishById(Dish dish);

    /**
     * 菜品总览
     * @param status
     * @return
     */
    @Select("select count(id) from dish where status = #{status}")
    Integer getDishCountByStatus(Integer status);
}
