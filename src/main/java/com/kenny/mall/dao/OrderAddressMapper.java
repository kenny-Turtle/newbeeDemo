package com.kenny.mall.dao;

import com.kenny.mall.entity.OrderAddress;

public interface OrderAddressMapper {

    /*
    新增地址
    * */
    int insertSelective(OrderAddress record);
}
