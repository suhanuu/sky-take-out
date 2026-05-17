package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计管理")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;
    /**
     * 营业额数据统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额数据统计")
    public Result<TurnoverReportVO> turnoverStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额数据统计：{}到{}", begin, end);
        TurnoverReportVO report = reportService.turnoverStatistics(begin, end);
        return Result.success(report);
    }

    /**
     * 用户数据统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户数据统计")
    public Result<UserReportVO> userStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("用户数据统计");
        UserReportVO report = reportService.userStatistics(begin, end);
        return Result.success(report);
    }
    /**
     * 订单数据统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单数据统计")
    public Result<OrderReportVO> ordersStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单数据统计");
        return Result.success(reportService.ordersStatistics(begin, end));
    }
    /**
     * 销量Top10
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("销量Top10")
    public Result<SalesTop10ReportVO> top10(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("销量Top10");
        return Result.success(reportService.top10(begin, end));
    }

    @GetMapping("/export")
    @ApiOperation("导出数据报表")
    public void export(HttpServletResponse response){
        log.info("导出数据报表");
        reportService.export(response);


    }


}
