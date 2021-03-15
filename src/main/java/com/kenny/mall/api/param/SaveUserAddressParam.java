package com.kenny.mall.api.param;

import lombok.Data;

/**
 * @Author zfj
 * @create 2021/3/15 14:09
 */
@Data
public class SaveUserAddressParam {
    private String userName;

    private String userPhone;

    private Byte defaultFlag;

    private String provinceName;

    private String cityName;

    private String regionName;

    private String detailAddress;
}
