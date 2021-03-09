package com.kenny.mall.dao;

import com.google.common.collect.Ordering;
import com.kenny.mall.entity.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {


    /*
    * 批量insert订单项数据
    * */
    int insertBatch(@Param("orderItems")List<OrderItem> orderItems);

    /*
    * 根据订单ID获取项单项列表
    * */
    List<OrderItem> selectByOrderId(Long orderId);


}
