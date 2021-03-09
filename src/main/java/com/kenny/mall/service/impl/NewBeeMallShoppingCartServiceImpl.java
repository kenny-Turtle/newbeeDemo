package com.kenny.mall.service.impl;

import com.kenny.mall.api.param.SaveCartItemParam;
import com.kenny.mall.api.vo.ShoppingCartItemVO;
import com.kenny.mall.common.Constants;
import com.kenny.mall.common.NewBeeMallException;
import com.kenny.mall.common.ServiceResultEnum;
import com.kenny.mall.dao.NewBeeMallGoodsMapper;
import com.kenny.mall.dao.ShoppingCartItemMapper;
import com.kenny.mall.entity.NewBeeMallGoods;
import com.kenny.mall.entity.ShoppingCartItem;
import com.kenny.mall.service.NewBeeMallShoppingCartService;
import com.kenny.mall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NewBeeMallShoppingCartServiceImpl implements NewBeeMallShoppingCartService {

    @Autowired
    private ShoppingCartItemMapper shoppingCartItemMapper;

    @Autowired
    private NewBeeMallGoodsMapper goodsMapper;
    @Override
    public String saveNewBeeMallCartItem(SaveCartItemParam saveCartItemParam, Long userId) {
        //根据userId和goodsId查找出对应到购物车
        ShoppingCartItem temp = shoppingCartItemMapper.selectByUserIdAndGoodsId(userId, saveCartItemParam.getGoodsId());
        if(temp != null){
            NewBeeMallException.fail(ServiceResultEnum.SHOPPING_CART_ITEM_EXIST_ERROR.getResult());
        }
        NewBeeMallGoods goods = goodsMapper.selectByPrimaryKey(saveCartItemParam.getGoodsId());
        if(goods  == null){
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = shoppingCartItemMapper.selectCountByUserId(userId);
        //购物车参数，商品数量判断
        //最小判断
        if(saveCartItemParam.getGoodsCount() < 1){
            return ServiceResultEnum.SHOPPING_CART_ITEM_NUMBER_ERROR.getResult();
        }
        //最大判断
        if(saveCartItemParam.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //已存在的购物车的商品数量，已到最大值
        if(totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        BeanUtil.copyProperties(saveCartItemParam,shoppingCartItem);
        shoppingCartItem.setUserId(userId);
        //保存记录
        if (shoppingCartItemMapper.insertSelective(shoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public List<ShoppingCartItemVO> getMyShoppingCartItems(Long userId) {
        List<ShoppingCartItemVO> vos = new ArrayList<>();
        List<ShoppingCartItem> Items = shoppingCartItemMapper.selectByUserId(userId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        //数据转化
        return getShoppingCartItemVOS(vos,Items);
    }

    @Override
    public List<ShoppingCartItemVO> getCartItemsForSettle(List<Long> cartItemIds, Long userId) {
        List<ShoppingCartItemVO> itemVOs = new ArrayList<>();
        List<ShoppingCartItem> items = shoppingCartItemMapper.selectByUserIdAndCartItemIds(userId, cartItemIds);
        if(CollectionUtils.isEmpty(itemVOs)){
            NewBeeMallException.fail("参数异常");
        }
        if(items.size() != cartItemIds.size()){
            NewBeeMallException.fail("参数异常");
        }

        return getShoppingCartItemVOS(itemVOs,items);
    }

    /*
    * 数据转换，详细类信息（shoppingCartItems）转换为页面显示的VO类（shoppingCartItemVOS）
    * 因为需要处理一下每条数据的 商品图片和商品价格 字段
    * */
    private List<ShoppingCartItemVO> getShoppingCartItemVOS(List<ShoppingCartItemVO> vos,List<ShoppingCartItem> items){
        if(!CollectionUtils.isEmpty(items)){
            //查询商品信息并做数据转换
            List<Long> goodsIds = items.stream().map(ShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<NewBeeMallGoods> goods = goodsMapper.selectByPrimaryKeys(goodsIds);
            Map<Long,NewBeeMallGoods> goodsMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(goods)){
                goodsMap = goods.stream().collect(Collectors.toMap(NewBeeMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (ShoppingCartItem item : items) {
                ShoppingCartItemVO vo = new ShoppingCartItemVO();
                BeanUtil.copyProperties(item, vo);
                if(goodsMap.containsKey(item.getGoodsId())){
                    NewBeeMallGoods temp = goodsMap.get(item.getGoodsId());
                    vo.setGoodsCoverImg(temp.getGoodsCoverImg());
                    String goodsName = temp.getGoodsName();
                    if(goodsName.length() > 28 ){
                        goodsName = goodsName.substring(0,28)+"...";
                    }
                    vo.setGoodsName(goodsName);
                    vo.setSellingPrice(temp.getSellingPrice());
                    vos.add(vo);
                }
            }
        }
        return vos;
    }
}
