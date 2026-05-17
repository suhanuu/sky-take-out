package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private ReportService reportService;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 获取业务数据统计
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDate begin, LocalDate end) {

        TurnoverReportVO turnoverReportVO = reportService.turnoverStatistics(begin, end);
        UserReportVO userReportVO = reportService.userStatistics(begin, end);
        OrderReportVO orderReportVO = reportService.ordersStatistics(begin, end);

        // 安全提取营业额（处理多日数据，需要累加）
        Double turnover = 0.0;
        if (turnoverReportVO != null && turnoverReportVO.getTurnoverList() != null
                && !turnoverReportVO.getTurnoverList().isEmpty()) {
            try {
                String[] turnoverArray = turnoverReportVO.getTurnoverList().split(",");
                double sum = 0.0;
                for (String t : turnoverArray) {
                    sum += Double.parseDouble(t.trim()); //trim()去除字符两端的空白字符
                }
                turnover = sum;
            } catch (NumberFormatException e) {
                log.warn("营业额解析失败: {}", turnoverReportVO.getTurnoverList());
            }
        }

        // 安全提取有效订单数
        Integer validOrderCount = 0;
        if (orderReportVO != null && orderReportVO.getValidOrderCount() != null) {
            validOrderCount = orderReportVO.getValidOrderCount();
        }

        // 计算客单价（避免除零）保留两位小数
        Double unitPrice = 0.0;
        if (validOrderCount > 0) {
            BigDecimal turnoverBigDecimal = BigDecimal.valueOf(turnover);
            BigDecimal validOrderCountBigDecimal = BigDecimal.valueOf(validOrderCount);
            BigDecimal unitPriceBigDecimal = turnoverBigDecimal.divide(validOrderCountBigDecimal, 2, BigDecimal.ROUND_HALF_UP);
            unitPrice = unitPriceBigDecimal.doubleValue();
        }

        // 安全提取订单完成率
        Double orderCompletionRate = 0.0;
        if (orderReportVO != null && orderReportVO.getOrderCompletionRate() != null) {
            orderCompletionRate = orderReportVO.getOrderCompletionRate();
        }

        // 安全提取新增用户数（处理多日数据，需要累加）
        Integer newUsers = 0;
        if (userReportVO != null && userReportVO.getNewUserList() != null
                && !userReportVO.getNewUserList().isEmpty()) {
            try {
                String[] newUserArray = userReportVO.getNewUserList().split(",");
                int sum = 0;
                for (String n : newUserArray) {
                    sum += Integer.parseInt(n.trim());
                }
                newUsers = sum;
            } catch (NumberFormatException e) {
                log.warn("新增用户数解析失败: {}", userReportVO.getNewUserList());
            }
        }
        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }


    /**
     * 套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO overviewSetmeals() {
        //1、查询已启售的套餐
        Integer sold = setmealMapper.getSetmealCountByStatus(StatusConstant.ENABLE);
        //2、查询已停售的套餐
        Integer discontinued = setmealMapper.getSetmealCountByStatus(StatusConstant.DISABLE);
        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 菜品总览
     * @return
     */
    @Override
    public DishOverViewVO overviewDishes() {
        //1、查询已启售的菜品
        Integer sold = dishMapper.getDishCountByStatus(StatusConstant.ENABLE);
        //2、查询已停售的菜品
        Integer discontinued = dishMapper.getDishCountByStatus(StatusConstant.DISABLE);
        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 订单总览
     * @return
     */
    @Override
    public OrderOverViewVO overviewOrders() {
        //1、待接单(2)
        Integer waitingOrders = orderMapper.countOrdersByStatus(Orders.TO_BE_CONFIRMED);;
        waitingOrders = waitingOrders == null ? 0 : waitingOrders;
        //2、待派送(3)
        Integer deliveredOrders = orderMapper.countOrdersByStatus(Orders.CONFIRMED);
        deliveredOrders = deliveredOrders == null ? 0 : deliveredOrders;
        //3、已完成(5)
        Integer completedOrders = orderMapper.countOrdersByStatus(Orders.COMPLETED);
        completedOrders = completedOrders == null ? 0 : completedOrders;
        //4、已取消(6)
        Integer cancelledOrders = orderMapper.countOrdersByStatus(Orders.CANCELLED);
        cancelledOrders = cancelledOrders == null ? 0 : cancelledOrders;
        //5、总订单数
        Integer allOrders = orderMapper.countOrders();
        allOrders = allOrders == null ? 0 : allOrders;
        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }
}
