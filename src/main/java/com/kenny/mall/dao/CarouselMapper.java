package com.kenny.mall.dao;

import com.kenny.mall.entity.Carousel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarouselMapper {

    List<Carousel> findCarouselsByNum(@Param("number") int number);
}
