package com.kenny.mall.api;

import com.kenny.mall.api.vo.NewBeeMallIndexCategoryVO;
import com.kenny.mall.common.NewBeeMallException;
import com.kenny.mall.common.ServiceResultEnum;
import com.kenny.mall.service.NewBeeMallCategoryService;
import com.kenny.mall.util.Result;
import com.kenny.mall.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "v1",tags = "新蜂商城分类页面接口")
@RequestMapping("/api/v1")
public class NewBeeMallGoodsCategoryAPI {


    @Autowired
    private NewBeeMallCategoryService newBeeMallCategoryService;

    @RequestMapping("/categories")
    @ApiOperation(value = "获取分类数据",notes = "分类页面使用")
    public Result<List<NewBeeMallIndexCategoryVO>> getCategory(){
        List<NewBeeMallIndexCategoryVO> categories = newBeeMallCategoryService.getCategoriesForIndex();
        if(CollectionUtils.isEmpty(categories)){
            NewBeeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(categories);
    }
}
