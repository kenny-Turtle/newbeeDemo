package com.kenny.mall.dao;

import com.kenny.mall.entity.NewBeeMallGoods;
import com.kenny.mall.entity.StockNumDTO;
import com.kenny.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NewBeeMallGoodsMapper {

    List<NewBeeMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    /*根据某个商品id获取商品详情*/
    NewBeeMallGoods selectByPrimaryKey(Long goodsId);

    /*根据分页工具，获取商品列表*/
    List<NewBeeMallGoods> findNewBeeMallGoodsListBySearch(PageQueryUtil pageUtil);

    /*根据分页工具，获取数据的总条数*/
    int getTotalNewBeeMallGoodsBySearch(PageQueryUtil pageUtil);

    /*测试，无参数*/
    List<NewBeeMallGoods> text(Map<String,Object> textMap);

    /*根据商品ID，更新库存*/
    int updateStockNum(@Param("stockNums")List<StockNumDTO> stockNums);
}
