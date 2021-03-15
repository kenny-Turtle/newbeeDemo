package com.kenny.mall.dao;

import com.kenny.mall.entity.MallUser;
import com.kenny.mall.entity.UserAddress;

import java.util.List;

public interface UserAddressMapper {


    UserAddress selectByPrimaryKey(Long addressId);

    List<UserAddress> findMyAddressList(Long userId);

    UserAddress getMyDefaultAddress(Long userId);

    int updateByPrimaryKeySelective(UserAddress record);

    int insertSelective(UserAddress record);

    int deletedByPrimaryKey(Long addressId);
}
