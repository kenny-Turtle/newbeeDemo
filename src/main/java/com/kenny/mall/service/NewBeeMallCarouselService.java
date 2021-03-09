package com.kenny.mall.service;

import com.kenny.mall.api.vo.NewBeeMallIndexCarouselVO;

import java.util.List;

public interface NewBeeMallCarouselService {

    /*
    返回固定数量的轮播图对象
     */
    public List<NewBeeMallIndexCarouselVO> getCarouselsForIndex(int number);

}
