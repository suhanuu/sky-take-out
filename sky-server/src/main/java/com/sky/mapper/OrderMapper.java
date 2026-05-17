package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 历史订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    // 根据id查询订单
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**`
     * 修改订单
     * @param orders
     */
    void update(Orders orders);

    //todo 根据订单号查询订单

    Orders getByNumber(String orderNumber);

    /**
     * 根据状态统计订单数量
     * @param status
     * @return
     */
    @Select("select count(*) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 条件搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 获取订单的统计数据
     * @param
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime time);

    /**
     * 根据状态统计订单数量
     * @param status
     * @return
     */
    Integer countOrdersByStatus(Integer status);

    /**
     * 统计总订单数
     * @return
     */
    @Select("select count(*) from orders")
    Integer countOrders();
}
