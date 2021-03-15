package com.kenny.mall.service.impl;

import com.kenny.mall.api.vo.UserAddressVO;
import com.kenny.mall.common.NewBeeMallException;
import com.kenny.mall.common.ServiceResultEnum;
import com.kenny.mall.dao.UserAddressMapper;
import com.kenny.mall.entity.UserAddress;
import com.kenny.mall.service.UserAddressService;
import com.kenny.mall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    @Override
    public List<UserAddressVO> getMyAddress(Long userId) {
        List<UserAddress> myAddressList = userAddressMapper.findMyAddressList(userId);
        List<UserAddressVO> userAddressVOS = BeanUtil.copyList(myAddressList,UserAddressVO.class);
        return userAddressVOS;
    }

    @Override
    public Boolean saveUserAddress(UserAddress userAddress) {
        Date now = new Date();
        if(userAddress.getDefaultFlag().intValue()==1){
            UserAddress defaultAddress = userAddressMapper.getMyDefaultAddress(userAddress.getUserId());
            if(defaultAddress != null){
                defaultAddress.setDefaultFlag((byte)0);
                defaultAddress.setUpdateTime(now);
                int result = userAddressMapper.updateByPrimaryKeySelective(defaultAddress);
                if(result < 1){
                    //未成功
                    NewBeeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
                }
            }
        }
        return userAddressMapper.insertSelective(userAddress) > 0;
    }

    @Override
    public Boolean updateUserAddress(UserAddress userAddress) {
        UserAddress tempAddress = getUserAddressById(userAddress.getUserId());
        Date now = new Date();
        if(userAddress.getDefaultFlag() == 1){
            UserAddress defaultAddress = userAddressMapper.getMyDefaultAddress(userAddress.getUserId());
            if(defaultAddress != null && !defaultAddress.getAddressId().equals(tempAddress)){
                //存在默认地址且地址并不是当前修改的地址
                defaultAddress.setDefaultFlag((byte)0);
                defaultAddress.setUpdateTime(now);
                int i = userAddressMapper.updateByPrimaryKeySelective(defaultAddress);
                if(i < 1){
                    NewBeeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
                }
            }
        }
        userAddress.setUpdateTime(now);
        return userAddressMapper.updateByPrimaryKeySelective(userAddress) > 0;
    }

    @Override
    public UserAddress getMyDefaultAddressByUserId(Long userId) {
        return userAddressMapper.getMyDefaultAddress(userId);
    }

    @Override
    public Boolean deletedById(Long addressId) {
        return userAddressMapper.deletedByPrimaryKey(addressId) > 0;
    }
}
