package com.kenny.mall.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class NewBeeMallIndexCategoryVO {

    @ApiModelProperty("分类id")
    private Long categoryId;

    @ApiModelProperty("分类等级")
    private Byte Level;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("二级分类列表")
    private List<SecondLevelCategoryVO> secondLevelCategoryVOS;

}
