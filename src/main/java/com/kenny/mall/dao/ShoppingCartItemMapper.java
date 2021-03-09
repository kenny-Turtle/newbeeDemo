package com.kenny.mall.dao;

import com.kenny.mall.api.vo.ShoppingCartItemVO;
import com.kenny.mall.entity.ShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShoppingCartItemMapper {

    ShoppingCartItem selectByUserIdAndGoodsId(@Param("UserId") Long UserId,@Param("goodsId") Long goodsId);

    Integer selectCountByUserId(Long userId);

    int insertSelective(ShoppingCartItem record);

    List<ShoppingCartItem> selectByUserId(@Param("userId")Long userId,@Param("number")int number);

    List<ShoppingCartItem> selectByUserIdAndCartItemIds(@Param("userId")Long userId,@Param("cartItemIds")List<Long> cartItemIds);

    int deleteBatch(List<Long> ids);



}
