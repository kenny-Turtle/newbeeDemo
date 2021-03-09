package com.kenny.mall.service.impl;

import com.kenny.mall.api.vo.NewBeeMallIndexCategoryVO;
import com.kenny.mall.api.vo.SecondLevelCategoryVO;
import com.kenny.mall.api.vo.ThirdLevelCategoryVO;
import com.kenny.mall.common.Constants;
import com.kenny.mall.common.NewBeeMallCategoryLevelEnum;
import com.kenny.mall.dao.GoodsCategoryMapper;
import com.kenny.mall.entity.GoodsCategory;
import com.kenny.mall.service.NewBeeMallCategoryService;
import com.kenny.mall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class NewBeeMallCategoryServiceImpl implements NewBeeMallCategoryService {

    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public List<NewBeeMallIndexCategoryVO> getCategoriesForIndex() {
        List<NewBeeMallIndexCategoryVO> newBeeMallIndexCategoryVOS = new ArrayList<>();
        //获取一级分类的固定数量的数据
        List<GoodsCategory> firstLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if(!CollectionUtils.isEmpty(firstLevelCategories)){
            List<Long> firstLevelCategoryIds = firstLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            //获取二级分类数据
            List<GoodsCategory> secondCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(firstLevelCategoryIds, NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
            if(!CollectionUtils.isEmpty(secondCategories)){
                List<Long> secondCategoryIds = secondCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                //获取三级分类数据
                List<GoodsCategory> thirdCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(secondCategoryIds, NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
                if(!CollectionUtils.isEmpty(thirdCategories)){
                    //根据parentid将thirdcategories分组
                    Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdCategories.stream().collect(groupingBy(GoodsCategory::getParentId));
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    //处理二级分类
                    for (GoodsCategory secondCategory : secondCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(secondCategory,secondLevelCategoryVO);
                        //如果该二级分类下有数据，则放到二级分类下
                        if(thirdLevelCategoryMap.containsKey(secondLevelCategoryVO.getCategoryId())){
                            //根据二级分类的id取出map里的list
                            List<GoodsCategory> tempGoodsCategories = thirdLevelCategoryMap.get(secondLevelCategoryVO.getCategoryId());
                            secondLevelCategoryVO.setThirdLevelCategoryVOS(BeanUtil.copyList(tempGoodsCategories, ThirdLevelCategoryVO.class));
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }
                    //处理一级分类
                    if(!CollectionUtils.isEmpty(secondLevelCategoryVOS)){
                        //把二级分类根据parentid和自身组成map
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        for (GoodsCategory firstLevelCategory : firstLevelCategories) {
                            NewBeeMallIndexCategoryVO newBeeMallIndexCategoryVO= new NewBeeMallIndexCategoryVO();
                            BeanUtil.copyProperties(firstLevelCategory,newBeeMallIndexCategoryVO);
                            if(secondLevelCategoryVOMap.containsKey(newBeeMallIndexCategoryVO.getCategoryId())){
                                List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap.get(newBeeMallIndexCategoryVO.getCategoryId());
                                newBeeMallIndexCategoryVO.setSecondLevelCategoryVOS(BeanUtil.copyList(tempGoodsCategories,SecondLevelCategoryVO.class));
                                newBeeMallIndexCategoryVOS.add(newBeeMallIndexCategoryVO);
                            }
                        }
                    }
                }
            }
            return newBeeMallIndexCategoryVOS;
        }else{
            return null;
        }
    }
}
