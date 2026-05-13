package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 判断当前菜品或套餐是否在购物车中
     * @param shoppingCart
     * @return
     */

    List<ShoppingCart> getDishOrSetmeal(ShoppingCart shoppingCart);

    /**
     * 更新购物车
     * @param shoppingCart
     */

    void update(ShoppingCart shoppingCart);

    /**
     * 插入菜品和套餐数据
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (user_id, dish_id, setmeal_id, dish_flavor, name, image, amount, create_time) values" +
            " (#{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{name}, #{image}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 删除购物车数据
     * @param shoppingCart
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteShoppingCart(ShoppingCart shoppingCart);

    /**
     * 查询当前用户的购物车数据
     * @param userId
     * @return
     */
    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> getShoppingCartData(Long userId);

    /**
     * 清空当前用户的购物车数据
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 批量插入购物车数据
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
