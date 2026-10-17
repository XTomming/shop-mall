package com.niit.shopmall.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.shopmall.entity.Orders;
import com.niit.shopmall.mapper.MemberMapper;
import com.niit.shopmall.mapper.OrdersMapper;
import com.niit.shopmall.mapper.ProductMapper;
import com.niit.shopmall.vo.DashboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 后台首页统计服务
 */
@Service
public class DashboardService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    public DashboardVO dashboard() {
        DashboardVO vo = new DashboardVO();
        vo.setMemberCount(memberMapper.selectCount(null));
        vo.setProductCount(productMapper.selectCount(null));
        vo.setOrderCount(ordersMapper.selectCount(null));
        vo.setTotalSales(sumSales(null, null));

        List<String> days = new ArrayList<>();
        List<Long> orderCountPerDay = new ArrayList<>();
        List<BigDecimal> salesPerDay = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            LocalDateTime start = day.atStartOfDay();
            LocalDateTime end = day.plusDays(1).atStartOfDay();
            days.add(day.format(formatter));
            Long count = ordersMapper.selectCount(Wrappers.<Orders>lambdaQuery()
                    .ge(Orders::getCreateTime, start)
                    .lt(Orders::getCreateTime, end));
            orderCountPerDay.add(count);
            salesPerDay.add(sumSales(start, end));
        }
        vo.setDays(days);
        vo.setOrderCountPerDay(orderCountPerDay);
        vo.setSalesPerDay(salesPerDay);
        return vo;
    }

    /**
     * 统计已支付订单销售额，可限定时间范围
     */
    private BigDecimal sumSales(LocalDateTime start, LocalDateTime end) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(SUM(pay_amount),0) AS total");
        wrapper.in("status", 1, 2, 3);
        if (start != null && end != null) {
            wrapper.ge("create_time", start).lt("create_time", end);
        }
        List<Map<String, Object>> maps = ordersMapper.selectMaps(wrapper);
        if (maps.isEmpty() || maps.get(0).get("total") == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(maps.get(0).get("total").toString());
    }
}
