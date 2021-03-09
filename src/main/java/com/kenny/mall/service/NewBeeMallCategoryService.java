package com.kenny.mall.service;

import com.kenny.mall.api.vo.NewBeeMallIndexCategoryVO;

import java.util.List;

public interface NewBeeMallCategoryService {

    //返回首页分类数据
    List<NewBeeMallIndexCategoryVO> getCategoriesForIndex();
}
