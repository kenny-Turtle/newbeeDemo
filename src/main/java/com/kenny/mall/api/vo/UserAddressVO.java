package com.kenny.mall.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author zfj
 * @create 2021/3/15 11:26
 */
@Data
public class UserAddressVO {
    private Long addressId;

    private Long userId;

    private String userName;

    private String userPhone;

    @ApiModelProperty("是否是默认地址 0-不是，1-是")
    private Byte defaultFlag;

    private String provinceName;

    private String cityName;

    private String regionName;

    private String detailAddress;
}
