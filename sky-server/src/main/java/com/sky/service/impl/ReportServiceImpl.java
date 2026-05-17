package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //创建日期集合
        List<LocalDate> dateTimeList = new ArrayList<>();
        dateTimeList.add(begin);
        while(!begin.equals(end))
        {
            begin = begin.plusDays(1);
            dateTimeList.add(begin);


        }
        String dateList = StringUtils.join(dateTimeList, ",");//2023-01-01,2023-01-02,2023-01-03
       //创建营业额集合
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateTimeList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

//            Map<String, Object> map = new HashMap<>();
//            map.put("begin", beginTime);
//            map.put("end", endTime);
//            map.put("status", Orders.COMPLETED);
            Double turnover = reportMapper.turnoverStatistics(beginTime, endTime, Orders.COMPLETED);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        //封装返回结果
        String turnoverListStr = StringUtils.join(turnoverList, ",");
        return TurnoverReportVO.builder()
                .dateList(dateList)
                .turnoverList(turnoverListStr)
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {

        //创建日期集合
        List<LocalDate> dateTimeList = new ArrayList<>();
        dateTimeList.add(begin);
        while(!begin.equals(end))
        {
            begin = begin.plusDays(1);
            dateTimeList.add(begin);
        }
        String dateList = StringUtils.join(dateTimeList, ",");
        //创建用户数量集合
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateTimeList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //查询总用户数
            Integer totalUser = reportMapper.totalUserStatistics(endTime);
            //查询新增用户数
            Integer newUser = reportMapper.newUserStatistics(beginTime, endTime);
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }
        String totalUserListStr = StringUtils.join(totalUserList, ",");
        String newUserListStr = StringUtils.join(newUserList, ",");

        return UserReportVO.builder()
                .dateList(dateList)
                .totalUserList(totalUserListStr)
                .newUserList(newUserListStr)
                .build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //创建日期集合
        List<LocalDate> dateTimeList = new ArrayList<>();
        dateTimeList.add(begin);
        while(!begin.equals(end))
        {
            begin = begin.plusDays(1);
            dateTimeList.add(begin);
        }
        String dateList = StringUtils.join(dateTimeList, ",");

        List<Integer> orderCountList = new ArrayList<>(); //每日订单数
        List<Integer> validOrderCountList = new ArrayList<>(); //每日有效订单数
        for (LocalDate date : dateTimeList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = reportMapper.orderCount(beginTime, endTime);
            Integer validOrderCount = reportMapper.validOrderCount(beginTime, endTime, Orders.COMPLETED);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        String orderCountListStr = StringUtils.join(orderCountList, ",");
        String validOrderCountListStr = StringUtils.join(validOrderCountList, ",");

        //订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //订单完成率
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0)
        {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();
        }

        return OrderReportVO.builder()
                .dateList(dateList)
                .orderCountList(orderCountListStr)
                .validOrderCountList(validOrderCountListStr)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 销量top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<SalesTop10ReportVO> list = reportMapper.getSalesTop10(beginTime, endTime,Orders.COMPLETED);

        List<String> nameList = new ArrayList<>();
        List<String> numberList = new ArrayList<>();
        for (SalesTop10ReportVO salesTop10ReportVO : list) {
            nameList.add(salesTop10ReportVO.getNameList());
            numberList.add(salesTop10ReportVO.getNumberList());
        }
        String nameListStr = StringUtils.join(nameList, ",");
        String numberListStr = StringUtils.join(numberList, ",");
        return SalesTop10ReportVO.builder()
                .nameList(nameListStr)
                .numberList(numberListStr)
                .build();
    }

    /**
     * 数据导出
     * @param response
     */
    @Override
    public void export(HttpServletResponse response) {
        //创建日期集合
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        BusinessDataVO businessDataVO = workSpaceService.getBusinessData(begin, end);

        //1、创建Excel
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //填充数据
            XSSFSheet sheet = excel.getSheetAt(0);
            //填充第二行数据
            sheet.getRow(1).getCell(1).setCellValue("时间：" + begin + "至" + end);
            //填充第四行数据
            sheet.getRow(3).getCell(2).setCellValue(businessDataVO.getTurnover());//营业额
            sheet.getRow(3).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());//订单完成率
            sheet.getRow(3).getCell(6).setCellValue(businessDataVO.getNewUsers());//新增用户数
            //填充第五行数据
            sheet.getRow(4).getCell(2).setCellValue(businessDataVO.getValidOrderCount());//有效订单数
            sheet.getRow(4).getCell(4).setCellValue(businessDataVO.getUnitPrice());//客单价

            LocalDate date = begin;
            //填充明细数据
            for(int i = 0; i < 30; i++)
            {
                date = begin.plusDays(i);
                //查询某一天的数据
                BusinessDataVO businessDataVO1 = workSpaceService.getBusinessData(date,date);
                sheet.getRow(7 + i).getCell(1).setCellValue(date.toString());//日期
                sheet.getRow(7 + i).getCell(2).setCellValue(businessDataVO1.getTurnover());//营业额
                sheet.getRow(7 + i).getCell(3).setCellValue(businessDataVO1.getValidOrderCount());//有效订单数
                sheet.getRow(7 + i).getCell(4).setCellValue(businessDataVO1.getOrderCompletionRate());//订单完成率
                sheet.getRow(7 + i).getCell(5).setCellValue(businessDataVO1.getUnitPrice());//客单价
                sheet.getRow(7 + i).getCell(6).setCellValue(businessDataVO1.getNewUsers());//新增用户数
            }
            //写入文件
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭流
            out.close();
            excel.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
