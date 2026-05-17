package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDate;

public interface WorkSpaceService {
    /**
     * 获取业务数据统计
     * @return
     */
    BusinessDataVO getBusinessData(LocalDate begin, LocalDate end);

    /**
     * 套餐总览
     * @return
     */
    SetmealOverViewVO overviewSetmeals();

    /**
     * 菜品总览
     * @return
     */
    DishOverViewVO overviewDishes();

    /**
     * 订单总览
     * @return
     */
    OrderOverViewVO overviewOrders();
}
