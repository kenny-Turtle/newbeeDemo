package com.kenny.mall.service;


import com.kenny.mall.api.vo.UserAddressVO;
import com.kenny.mall.entity.UserAddress;

import java.util.List;

public interface UserAddressService {



    /*
    * 获取用户地址
    * */
    UserAddress getUserAddressById(Long addressId);

    /*
    * 获取我的收货地址
    * */
    List<UserAddressVO> getMyAddress(Long userId);

    /*
    * 保存收货地址
    * */
    Boolean saveUserAddress(UserAddress userAddress);

    /*
    * 修改收货地址
    * */
    Boolean updateUserAddress(UserAddress userAddress);

    /*
    * 获取我的收货地址
    * */
    UserAddress getMyDefaultAddressByUserId(Long userId);

    /*
    * 删除收货地址
    * */
    Boolean deletedById(Long addressId);
}
