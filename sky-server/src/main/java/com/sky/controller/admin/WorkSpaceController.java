package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@Api(tags = "工作台相关接口")
public class WorkSpaceController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 获取业务数据统计
     * @return
     */
    @GetMapping("/businessData")
    @ApiOperation("获取业务数据统计")
    public Result<BusinessDataVO> getBusinessData(){
        log.info("获取业务数据统计");
        LocalDate begin = LocalDate.now();
        LocalDate end = LocalDate.now();
        BusinessDataVO businessDataVO = workSpaceService.getBusinessData(begin, end);
        return Result.success(businessDataVO);
    }

    @GetMapping("/overviewSetmeals")
    @ApiOperation("查询套餐总览")
    public Result<SetmealOverViewVO> overviewSetmeals(){
        log.info("查询套餐总览");
        SetmealOverViewVO setmealOverViewVO = workSpaceService.overviewSetmeals();
        return Result.success(setmealOverViewVO);
    }

    /**
     * 查询菜品总览
     * @return
     */
    @GetMapping("/overviewDishes")
    @ApiOperation("查询菜品总览")
    public Result<DishOverViewVO> overviewDishes(){
        log.info("查询菜品总览");
        DishOverViewVO dishOverViewVO = workSpaceService.overviewDishes();
        return Result.success(dishOverViewVO);
    }

    /**
     * 查询订单总览
     * @return
     */
    @GetMapping("overviewOrders")
    @ApiOperation("查询订单总览")
    public Result<OrderOverViewVO> overviewOrders(){
        log.info("查询订单总览");
        OrderOverViewVO orderOverViewVO = workSpaceService.overviewOrders();
        return Result.success(orderOverViewVO);
    }
}
