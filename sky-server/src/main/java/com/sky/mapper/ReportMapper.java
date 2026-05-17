package com.sky.mapper;

import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    /**
     * 统计指定时间区间内的营业额数据
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    Double turnoverStatistics(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 统计指定时间区间内的用户总数量数量
     * @param endTime
     * @return
     */
    @Select("select count(*) from user where create_time < #{endTime} ")
    Integer totalUserStatistics(LocalDateTime endTime);

    /**
     * 统计一天中新增用户的数量
     * @param beginTime
     * @param endTime
     * @return
     */

    Integer newUserStatistics(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 每日订单总数
     * @param beginTime
     * @param endTime
     * @return
     */
    
    Integer orderCount(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 每日有效订单数
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    Integer validOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 统计指定时间区间内的销量排名top10
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    List<SalesTop10ReportVO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime, Integer status);
}
