package com.kenny.mall.api.vo;

import lombok.Data;

/*
*   搜索列表页商品VO
* */
@Data
public class NewBeeMallSearchGoodsVO {

    private Long goodsId;

    private String goodsName;

    private String goodsIntro;

    private String goodsCoversImg;

    private Integer sellingPrice;


}
