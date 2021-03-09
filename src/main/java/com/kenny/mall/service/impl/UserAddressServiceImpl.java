package com.kenny.mall.service.impl;

import com.kenny.mall.common.NewBeeMallException;
import com.kenny.mall.common.ServiceResultEnum;
import com.kenny.mall.dao.UserAddressMapper;
import com.kenny.mall.entity.UserAddress;
import com.kenny.mall.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public UserAddress getUserAddressById(Long addressId) {
        UserAddress userAddress = userAddressMapper.selectByPrimaryKey(addressId);
        if(userAddress == null){
            NewBeeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return userAddress;
    }
}
