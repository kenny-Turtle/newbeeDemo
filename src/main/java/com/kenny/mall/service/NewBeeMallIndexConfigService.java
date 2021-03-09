package com.kenny.mall.service;

import com.kenny.mall.api.vo.NewBeeMallIndexConfigGoodsVO;

import java.util.List;

public interface NewBeeMallIndexConfigService {

    List<NewBeeMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType,int number);

}
