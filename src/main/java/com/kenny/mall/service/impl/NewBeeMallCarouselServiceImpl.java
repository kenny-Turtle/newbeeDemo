package com.kenny.mall.service.impl;

import com.kenny.mall.api.vo.NewBeeMallIndexCarouselVO;
import com.kenny.mall.dao.CarouselMapper;
import com.kenny.mall.entity.Carousel;
import com.kenny.mall.service.NewBeeMallCarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
@Service
public class NewBeeMallCarouselServiceImpl implements NewBeeMallCarouselService {

    @Autowired
    private CarouselMapper carouselMapper;
    @Override
    public List<NewBeeMallIndexCarouselVO> getCarouselsForIndex(int number) {
        List<NewBeeMallIndexCarouselVO> newBeeMallIndexCarouselVOS=new ArrayList<>(number);
        //根据number获取轮播图
        List<Carousel> carousels = carouselMapper.findCarouselsByNum(number);
        if(!CollectionUtils.isEmpty(carousels)){
            //newBeeMallIndexCarouselVOS = BeanUtil.copyList(carousels,NewBeeMallIndexCarouselVO.class);
            NewBeeMallIndexCarouselVO temp = new NewBeeMallIndexCarouselVO();
            for (Carousel carousel : carousels) {
                temp.setCarouseUrl(carousel.getCarouselUrl());
                temp.setRedirectUrl(carousel.getRedirectUrl());
                newBeeMallIndexCarouselVOS.add(temp);
            }
        }
        return newBeeMallIndexCarouselVOS;
    }
}
