package com.kenny.mall.service.impl;

import com.kenny.mall.api.vo.NewBeeMallIndexConfigGoodsVO;
import com.kenny.mall.dao.IndexConfigMapper;
import com.kenny.mall.dao.NewBeeMallGoodsMapper;
import com.kenny.mall.entity.IndexConfig;
import com.kenny.mall.entity.NewBeeMallGoods;
import com.kenny.mall.service.NewBeeMallIndexConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.kenny.mall.util.BeanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class NewBeeMallIndexConfigServiceImpl implements NewBeeMallIndexConfigService {

    @Autowired
    private IndexConfigMapper indexConfigMapper;
    @Autowired
    private NewBeeMallGoodsMapper newBeeMallGoodsMapper;
    @Override
    public List<NewBeeMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number) {
        List<NewBeeMallIndexConfigGoodsVO> newBeeMallIndexConfigGoodsVOS=new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(configType, number);
        if(!CollectionUtils.isEmpty(indexConfigs)){
            //取出所有的goodsid
            List<Long> goodsIds=indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<NewBeeMallGoods> newBeeMallGoods = newBeeMallGoodsMapper.selectByPrimaryKeys(goodsIds);
            newBeeMallIndexConfigGoodsVOS = BeanUtil.copyList(newBeeMallGoods,NewBeeMallIndexConfigGoodsVO.class);
            for(NewBeeMallIndexConfigGoodsVO newBeeMallIndexConfigGoodsVO : newBeeMallIndexConfigGoodsVOS){
                String goodsName = newBeeMallIndexConfigGoodsVO.getGoodsName();
                if(goodsName.length()>30){
                    goodsName = goodsName.substring(0,30)+"....";
                    newBeeMallIndexConfigGoodsVO.setGoodsName(goodsName);
                }
            }
        }
        return newBeeMallIndexConfigGoodsVOS;
    }
}
