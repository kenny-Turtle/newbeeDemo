package com.kenny.mall.service;

import com.kenny.mall.api.param.SaveCartItemParam;
import com.kenny.mall.api.vo.ShoppingCartItemVO;

import java.util.List;

public interface NewBeeMallShoppingCartService {

    /*
    * 保存商品至购物车中
    * */
    String saveNewBeeMallCartItem(SaveCartItemParam saveCartItemParam,Long userId);

    /**
     * 根据userId获取购物车列表数据
     */
    List<ShoppingCartItemVO> getMyShoppingCartItems(Long UserId);

    /*
    * 根据userId和购物车对cartItemIds，获取到对应对购物项明细
    * */
    List<ShoppingCartItemVO> getCartItemsForSettle(List<Long> cartItemIds,Long userId);


}
