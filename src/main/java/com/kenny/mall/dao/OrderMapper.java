package com.kenny.mall.dao;

import com.kenny.mall.entity.Order;
import com.kenny.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {

    int insertSelective(Order record);

    Order selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(Order record);

    int getTotalOrders(PageQueryUtil pageUtil);

    List<Order> findOrderList(PageQueryUtil pageUtil);

    int closeOrder(@Param("orderIds")List<Long> orderIds,@Param("orderStatus") int orderStatus);


}
