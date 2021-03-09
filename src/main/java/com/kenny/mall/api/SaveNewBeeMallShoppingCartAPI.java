package com.kenny.mall.api;


import com.kenny.mall.api.param.SaveCartItemParam;
import com.kenny.mall.api.vo.ShoppingCartItemVO;
import com.kenny.mall.common.NewBeeMallException;
import com.kenny.mall.common.ServiceResultEnum;
import com.kenny.mall.config.annotation.TokenToMallUser;
import com.kenny.mall.entity.MallUser;
import com.kenny.mall.service.NewBeeMallShoppingCartService;
import com.kenny.mall.util.Result;
import com.kenny.mall.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationEvent;
import java.util.Arrays;
import java.util.List;

@RestController
@Api(value = "v1",tags = "购物车相关接口")
@RequestMapping("/api/v1")
public class SaveNewBeeMallShoppingCartAPI {

    @Autowired
    private NewBeeMallShoppingCartService shoppingCartService;

    @PostMapping("/shop-cart")
    @ApiOperation(value = "添加商品到购物车接口",notes = "传参为商品id，数量")
    public Result saveNewBeeMallShoppingCartItem(@RequestBody SaveCartItemParam saveCartItemParam,
                                                 @TokenToMallUser MallUser loginMallUser){
        String saveResult = shoppingCartService.saveNewBeeMallCartItem(saveCartItemParam, loginMallUser.getUserId());
        //添加成功
        if(ServiceResultEnum.SUCCESS.getResult().equals(saveResult)){
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @GetMapping("/shop-cart")
    @ApiOperation(value = "购物车列表",notes = "")
    public Result<ShoppingCartItemVO> cartItemList(@TokenToMallUser MallUser mallUser){
        return ResultGenerator.genSuccessResult(shoppingCartService.getMyShoppingCartItems(mallUser.getUserId()));
    }

    @GetMapping("/shop-cart/settle")
    @ApiOperation(value = "根据购物项id获取购物项明细",notes = "确认订单页面使用")
    public Result<List<ShoppingCartItemVO>> toSettle(Long[] cartItemIds,@TokenToMallUser MallUser mallUser){
        if(cartItemIds.length<1){
            NewBeeMallException.fail("参数异常");
        }
        int priceTotal = 0;
        System.out.println(mallUser.getUserId()+"<<<<<<<<<<<<<<<<<");
        //调用接口，根据购物项id集合和用户id，获取到购物项详细信息
        List<ShoppingCartItemVO> itemsForSettle = shoppingCartService.getCartItemsForSettle(Arrays.asList(cartItemIds), mallUser.getUserId());
        //对购物项明细进行校验，并算出总价
        if(CollectionUtils.isEmpty(itemsForSettle)){
            NewBeeMallException.fail("参数异常");
        }else{
            for (ShoppingCartItemVO vo : itemsForSettle) {
                priceTotal += vo.getGoodsCount() * vo.getSellingPrice();
            }
            if(priceTotal < 1){
                NewBeeMallException.fail("价格异常");
            }
        }
        return ResultGenerator.genSuccessResult(itemsForSettle);
    }
}
