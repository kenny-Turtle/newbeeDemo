package com.kenny.mall.dao;

import com.kenny.mall.entity.UserAddress;

public interface UserAddressMapper {


    UserAddress selectByPrimaryKey(Long addressId);
}
