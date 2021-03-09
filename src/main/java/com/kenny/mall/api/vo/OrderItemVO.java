package com.kenny.mall.api.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderItemVO implements Serializable {
    private Long goodsId;

    private Integer goodsCount;

    private String goodsName;

    private String goodsCoverImg;

    private Integer sellingPrice;
}
