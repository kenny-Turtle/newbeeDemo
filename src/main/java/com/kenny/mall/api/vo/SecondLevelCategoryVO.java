package com.kenny.mall.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SecondLevelCategoryVO {

    @ApiModelProperty("当前分类id")
    private Long categoryId;

    @ApiModelProperty("父级id")
    private Long parentId;

    @ApiModelProperty("当前分类等级")
    private Byte Level;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("三级分类列表")
    private List<ThirdLevelCategoryVO> thirdLevelCategoryVOS;
}
