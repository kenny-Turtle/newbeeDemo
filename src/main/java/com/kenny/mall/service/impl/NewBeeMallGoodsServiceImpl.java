package com.kenny.mall.service.impl;

import com.kenny.mall.api.vo.NewBeeMallSearchGoodsVO;
import com.kenny.mall.dao.NewBeeMallGoodsMapper;
import com.kenny.mall.entity.NewBeeMallGoods;
import com.kenny.mall.service.NewBeeMallGoodsService;
import com.kenny.mall.util.BeanUtil;
import com.kenny.mall.util.PageQueryUtil;
import com.kenny.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewBeeMallGoodsServiceImpl implements NewBeeMallGoodsService {

    @Autowired
    private NewBeeMallGoodsMapper goodsMapper;



    @Override
    public PageResult searchNewBeeMallGoods(PageQueryUtil pageUtil) {
        List<NewBeeMallGoods> goodsList = goodsMapper.findNewBeeMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalNewBeeMallGoodsBySearch(pageUtil);
        List<NewBeeMallSearchGoodsVO> newBeeMallSearchGoodsVOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(goodsList)){
            newBeeMallSearchGoodsVOS = BeanUtil.copyList(goodsList, NewBeeMallSearchGoodsVO.class);
            for (NewBeeMallSearchGoodsVO vo : newBeeMallSearchGoodsVOS) {
                String goodsName = vo.getGoodsName();
                String goodsIntro = vo.getGoodsIntro();
                if(goodsName.length()>28){
                    goodsName = goodsName.substring(0,28) + "...";
                    vo.setGoodsName(goodsName);
                }
                if(goodsIntro.length()>30){
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    vo.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(total,pageUtil.getLimit(),pageUtil.getPage(),newBeeMallSearchGoodsVOS);
        return pageResult;

    }

    @Override
    public NewBeeMallGoods getNewBeeMallGoodsById(Long goodsId) {
        return goodsMapper.selectByPrimaryKey(goodsId);
    }
}
