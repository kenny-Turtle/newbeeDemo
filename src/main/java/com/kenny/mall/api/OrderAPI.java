package com.kenny.mall.api;

import com.kenny.mall.api.param.SaveOrderParam;
import com.kenny.mall.api.vo.OrderDetailVO;
import com.kenny.mall.api.vo.ShoppingCartItemVO;
import com.kenny.mall.common.NewBeeMallException;
import com.kenny.mall.common.ServiceResultEnum;
import com.kenny.mall.config.annotation.TokenToMallUser;
import com.kenny.mall.dao.OrderItemMapper;
import com.kenny.mall.dao.ShoppingCartItemMapper;
import com.kenny.mall.entity.MallUser;
import com.kenny.mall.entity.UserAddress;
import com.kenny.mall.service.NewBeeMallShoppingCartService;
import com.kenny.mall.service.OrderService;
import com.kenny.mall.service.UserAddressService;
import com.kenny.mall.util.Result;
import com.kenny.mall.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@RestController
@Api(value = "v1",tags = "订单操作相关接口")
@RequestMapping("/api/v1")
public class OrderAPI {

    @Autowired
    private NewBeeMallShoppingCartService shoppingCartService;

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemMapper orderItemMapper;


    @PostMapping("/saveOrder")
    @ApiOperation(value = "生成订单接口",notes = "传参为地址id和待结算的购物项商品id")
    public Result<String> saveOrder(@ApiParam(value = "订单参数")@RequestBody SaveOrderParam saveOrderParam, @TokenToMallUser MallUser mallUser){
        int priceTotal = 0;
        if(saveOrderParam == null || saveOrderParam.getCartItemIds() == null || saveOrderParam.getAddressId() == null){
            NewBeeMallException.fail(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        if(saveOrderParam.getCartItemIds().length < 1){
            NewBeeMallException.fail(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        List<ShoppingCartItemVO> itemForSave = shoppingCartService.getCartItemsForSettle(Arrays.asList(saveOrderParam.getCartItemIds()), mallUser.getUserId());
        if (CollectionUtils.isEmpty(itemForSave)) {
            NewBeeMallException.fail("参数异常");
        }else{
            //总价
            for (ShoppingCartItemVO item : itemForSave) {
                priceTotal += item.getSellingPrice() * item.getGoodsCount();
            }
            if(priceTotal < 1){
                NewBeeMallException.fail("价格异常");
            }
            UserAddress address = userAddressService.getUserAddressById(mallUser.getUserId());
            if(!mallUser.getUserId().equals(address.getUserId())){
                NewBeeMallException.fail(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
            }
            //保存订单并返回订单号
            String saveOrderResult = orderService.saveOrder(mallUser, address, itemForSave);
            Result result = new Result();
            result.setData(saveOrderParam);
            return result;
        }
        return ResultGenerator.genFailResult("生成订单失败");
    }

    @GetMapping("order/{orderNo}")
    @ApiOperation(value = "订单详情接口",notes = "传参为订单号")
    public Result<OrderDetailVO> orderDetailPage(@ApiParam(value = "订单号") String orderNo,@TokenToMallUser MallUser user){

    }

    @GetMapping("/paysuccess")
    @ApiOperation(value = "模拟支付宝回调的接口", notes = "传参为订单号和支付方式")
    public Result paySuccess(@ApiParam(value = "订单号") @RequestParam("orderNo") String orderNo,@ApiParam(value = "支付方式") @RequestParam("payType") int payType){
        String payResult = orderService.paySuccess(orderNo, payType);
        if(ServiceResultEnum.SUCCESS.getResult().equals(payResult)){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult(payResult);
        }
    }

    @PutMapping("/order/{orderNo}/cancel")
    @ApiOperation(value = "订单取消接口",notes = "参数未订单号")
    public Result cancelOrder(@ApiParam(value = "订单号")@PathVariable("orderNo") String orderNo,@TokenToMallUser MallUser mallUser){

        String cancelOrderResult = orderService.cancelOrder(orderNo, mallUser.getUserId());
        if(ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    @PutMapping("/order/{orderNo}/finish")
    @ApiOperation(value = "确认收获接口", notes = "参数为订单号")
    public Result finishOrder(@ApiParam(value = "订单号")@PathVariable("orderNo")String orderNo,@TokenToMallUser MallUser mallUser){
        String finishOrderResult = orderService.finishOrder(orderNo, mallUser.getUserId());
        if(ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

}






