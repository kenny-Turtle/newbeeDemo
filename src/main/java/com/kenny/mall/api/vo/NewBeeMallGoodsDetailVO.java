package com.kenny.mall.api.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NewBeeMallGoodsDetailVO implements Serializable {
    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("简介")
    private String goodsIntro;
    @ApiModelProperty("图片地址")
    private String goodsCoverImg;
    @ApiModelProperty("商品价格")
    private Integer sellingPrice;
    @ApiModelProperty("商品标签")
    private String tag;
    @ApiModelProperty("商品图片")
    private String[] goodsCarouseList;
    @ApiModelProperty("商品原价")
    private Integer originalPrice;
    @ApiModelProperty("商品详情字段")
    private String goodsDetailContent;
}
