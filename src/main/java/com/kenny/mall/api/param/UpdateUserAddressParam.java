package com.kenny.mall.api.param;

import lombok.Data;

import javax.swing.*;

/**
 * @Author zfj
 * @create 2021/3/15 14:58
 */
@Data
public class UpdateUserAddressParam {
    private Long addressId;

    private Long userId;

    private String userName;

    private String userPhone;

    private String defaultFlag;

    private String provinceName;

    private String cityName;

    private String regionName;

    private String detailAddress;
}
