package com.kenny.mall.api.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SaveCartItemParam {
    @ApiModelProperty("商品数量")
    public Integer goodsCount;

    @ApiModelProperty("商品ID")
    public Long goodsId;
}
