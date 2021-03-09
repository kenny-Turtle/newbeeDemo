package com.kenny.mall.service;

import com.kenny.mall.entity.NewBeeMallGoods;
import com.kenny.mall.util.PageQueryUtil;
import com.kenny.mall.util.PageResult;

public interface NewBeeMallGoodsService {

    /*
    * 商品搜索
    * */
    PageResult searchNewBeeMallGoods(PageQueryUtil pageUtil);

    /*根据商品id获取商品
    * */
    NewBeeMallGoods getNewBeeMallGoodsById(Long goodsId);
}
