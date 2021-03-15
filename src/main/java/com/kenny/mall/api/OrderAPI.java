package com.kenny.mall.api;

import com.kenny.mall.api.param.SaveOrderParam;
import com.kenny.mall.api.vo.OrderDetailVO;
import com.kenny.mall.api.vo.OrderListVO;
import com.kenny.mall.api.vo.ShoppingCartItemVO;
import com.kenny.mall.common.Constants;
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
import com.kenny.mall.util.PageQueryUtil;
import com.kenny.mall.util.PageResult;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return null;
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

    @GetMapping("/order")
    @ApiOperation(value = "订单列表接口",notes = "传参为页码")
    public Result<PageResult<List<OrderListVO>>> orderList(
            @ApiParam(value = "页码")@RequestParam(required = false) Integer pageNumber,
            @ApiParam(value = "订单状态：0待支付。1待确认。2待发货。3已发货。4交易成功")@RequestParam(required = false) Integer status,
            @TokenToMallUser MallUser mallUser){
        Map params = new HashMap(4);
        if(pageNumber == null || pageNumber < 1){
            pageNumber = 1;
        }
        params.put("userId", mallUser.getUserId());
        params.put("orderStatus",status);
        params.put("page",pageNumber);
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(orderService.getMyOrder(pageQueryUtil));
    }

    @PutMapping("/order/{orderNo}/cancel")
    @ApiOperation(value = "订单取消接口",notes = "参数为订单号")
    public Result cancelOrder(@ApiParam(value = "订单号")@PathVariable("orderNo")String orderNo,@TokenToMallUser MallUser mallUser){
        String cancelResult = orderService.cancelOrder(orderNo, mallUser.getUserId());
        if(ServiceResultEnum.SUCCESS.getResult().equals(cancelResult)){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult(cancelResult);
        }
    }


}
