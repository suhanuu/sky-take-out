package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    //订单超时取消
    @Scheduled(cron = "0 * * * * * ")//每分钟执行一次
//    @Scheduled(cron = "0/5 * * * * *")
    public void cancelOrder(){
        log.info("订单取消");
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        //查询待支付订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, time);
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED); //设置订单状态为取消
                orders.setCancelReason("支付超时，取消订单");//设置取消原因
                orders.setCancelTime(LocalDateTime.now());//设置取消时间
                orderMapper.update(orders);
            }
        }

    }

    //每天凌晨一点自动确认上一天还在配送中的订单
    @Scheduled(cron = "0 0 1 * * ? ")//每天凌晨1点执行
    public void complete(){
        log.info("订单完成");
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        //查询待支付订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED); //设置订单状态为完成
                orders.setDeliveryTime(LocalDateTime.now()); //todo 应该设置完成时间吗
                orderMapper.update(orders);
            }
        }
    }
}
