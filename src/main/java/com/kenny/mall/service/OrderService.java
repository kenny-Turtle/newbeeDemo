package com.kenny.mall.service;

import com.kenny.mall.api.vo.OrderDetailVO;
import com.kenny.mall.api.vo.ShoppingCartItemVO;
import com.kenny.mall.entity.MallUser;
import com.kenny.mall.entity.UserAddress;
import com.kenny.mall.util.PageQueryUtil;
import com.kenny.mall.util.PageResult;
import com.kenny.mall.util.Result;

import java.util.List;

public interface OrderService {


    /*
    * 保存订单，返回单号
    * */
    String saveOrder(MallUser user, UserAddress address, List<ShoppingCartItemVO> itemVOList);

    /*
    * 订单号和支付方式，返回结果
    * */
    String paySuccess(String orderNo,int payType);

    /*
    * 根据订单号和用户id获取订单信息
    * */
    OrderDetailVO getOrderDetailByOrderNo(String orderNo,Long userId);

    /*
    * 我的订单列表
    * */
    PageResult getMyOrder(PageQueryUtil pageQueryUtil);

}
