package com.kenny.mall.dao;

import com.kenny.mall.entity.GoodsCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsCategoryMapper {


    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(@Param("parentIds") List<Long> parentIds,
                                                           @Param("categoryLevel") int categoryLevel,
                                                           @Param("number") int number);
}
