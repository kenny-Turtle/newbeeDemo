package com.kenny.mall.service.impl;

import com.kenny.mall.api.vo.OrderDetailVO;
import com.kenny.mall.api.vo.OrderItemVO;
import com.kenny.mall.api.vo.OrderListVO;
import com.kenny.mall.api.vo.ShoppingCartItemVO;
import com.kenny.mall.common.*;
import com.kenny.mall.dao.*;
import com.kenny.mall.entity.*;
import com.kenny.mall.service.OrderService;
import com.kenny.mall.util.BeanUtil;
import com.kenny.mall.util.NumberUtil;
import com.kenny.mall.util.PageQueryUtil;
import com.kenny.mall.util.PageResult;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    private NewBeeMallGoodsMapper goodsMapper;

    @Autowired
    private ShoppingCartItemMapper shoppingCartItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderAddressMapper orderAddressMapper;

    @Override
    @Transactional
    public String saveOrder(MallUser user, UserAddress address, List<ShoppingCartItemVO> itemVOList) {
        List<Long> itemIdList = itemVOList.stream().map(ShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = itemVOList.stream().map(ShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<NewBeeMallGoods> goods = goodsMapper.selectByPrimaryKeys(goodsIds);
        List<NewBeeMallGoods> goodsListNotSelling = goods.stream().filter(temp -> temp.getGoodsSellStatus() != Constants.SELL_STATUS_UP).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(goodsListNotSelling)){
            NewBeeMallException.fail(goodsListNotSelling.get(0).getGoodsName()+"已经下架，无法生成订单");
        }
        Map<Long, NewBeeMallGoods> goodsMap =
                goods.stream().collect(Collectors.toMap(NewBeeMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断库存
        for (ShoppingCartItemVO itemVO : itemVOList) {
            //如果商品中不存在购物车里的商品id，直接返回错误提醒
            if(!goodsMap.containsKey(itemVO.getGoodsId())){
                NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //如果存在数量大于库存的情况，直接返回错误提醒
            if(itemVO.getGoodsCount() > goodsMap.get(itemVO.getGoodsId()).getStockNum()){
                NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if(!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(goods)){
            if(shoppingCartItemMapper.deleteBatch(itemIdList) > 0){
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(itemVOList, StockNumDTO.class);
                int stockNumResult = goodsMapper.updateStockNum(stockNumDTOS);
                if(stockNumResult < 1){
                    NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                Order order = new Order();
                order.setOrderNo(orderNo);
                order.setUserId(user.getUserId());
                //总价
                for (ShoppingCartItemVO item : itemVOList) {
                    priceTotal += item.getSellingPrice() * item.getGoodsCount();
                }
                if(priceTotal < 1){
                    NewBeeMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                order.setTotalPrice(priceTotal);
                String extraInfo = "";
                order.setExtraInfo(extraInfo);
                //生成订单项并保存订单项记录
                if(orderMapper.insertSelective(order) > 0){
                    //生成订单收货地址快照，并保存至数据库
                    OrderAddress orderAddress = new OrderAddress();
                    BeanUtil.copyProperties(address, orderAddress);
                    orderAddress.setOrderId(order.getOrderId());
                    //生成所有订单项快照，并保存至数据库
                    List<OrderItem> orderItems = new ArrayList<>();
                    for (ShoppingCartItemVO item : itemVOList) {
                        OrderItem orderItem = new OrderItem();
                        BeanUtil.copyProperties(item,orderItem);
                        orderItem.setOrderId(order.getOrderId());
                        orderItems.add(orderItem);
                    }
                    //保存至数据库
                    if(orderItemMapper.insertBatch(orderItems) > 0 && orderAddressMapper.insertSelective(orderAddress) > 0){
                        //所有操作成功后，将订单号返回，以供controller方法跳转到订单详情
                        return orderNo;
                    }
                    NewBeeMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                NewBeeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            NewBeeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null){
            if(order.getOrderStatus().intValue() != OrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()){
                NewBeeMallException.fail("待支付状态下的订单无法支付");
            }
            order.setOrderStatus((byte) OrderStatusEnum.OREDER_PAID.getOrderStatus());
            order.setPayType((byte) payType);
            order.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            order.setPayTime(new Date());
            order.setUpdateTime(new Date());
            if(orderMapper.insertSelective(order) > 0){
                return ServiceResultEnum.SUCCESS.getResult();
            }else{
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public OrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            NewBeeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        if(!order.getUserId().equals(userId)){
            NewBeeMallException.fail(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getOrderId());
        //获取订单项数据
        if(!CollectionUtils.isEmpty(orderItems)){
            List<OrderItemVO> orderItemVOS = BeanUtil.copyList(orderItems,OrderItemVO.class);
            OrderDetailVO orderDetailVO = new OrderDetailVO();
            BeanUtil.copyProperties(order,orderDetailVO);
            orderDetailVO.setOrderStatusString(OrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(orderDetailVO.getOrderStatus()).getName());
            orderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(orderDetailVO.getPayType()).getName());
            orderDetailVO.setOrderItemVOS(orderItemVOS);
            return orderDetailVO;
        }else{
            NewBeeMallException.fail(ServiceResultEnum.ORDER_ITEM_NULL_ERROR.getResult());
            return null;
        }
    }

    @Override
    public PageResult getMyOrder(PageQueryUtil pageQueryUtil) {
        int total = orderMapper.getTotalOrders(pageQueryUtil);
        List<Order> orders = orderMapper.findOrderList(pageQueryUtil);
        List<OrderListVO> orderListVOS = new ArrayList<>();
        if(total >0){
            //数据转换，将实体类转为vo
            orderListVOS = BeanUtil.copyList(orders,OrderListVO.class);
            // 设置订单状态中文显示值
            for (OrderListVO orderListVO : orderListVOS) {
                orderListVO.setOrderStatusString(OrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(orderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = orders.stream().map(Order::getOrderId).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(orderIds)){
                List<OrderItem> orderItems = orderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<OrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(OrderItem::getOrderId));
                for (OrderListVO orderListVO : orderListVOS) {
                    //封装每个订单列表的订单项数据
                    if(itemByOrderIdMap.containsKey(orderListVO.getOrderId())){
                        List<OrderItem> orderItemTemp = itemByOrderIdMap.get(orderListVO.getOrderId());
                        //将orderItem对象列表转换成orderItemVO对象列表
                        List<OrderItemVO> orderItemVOS = BeanUtil.copyList(orderItemTemp,OrderItemVO.class);
                        orderListVO.setOrderItemVOList(orderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(pageQueryUtil.getPage(),total,pageQueryUtil.getLimit(),orderListVOS);
        return pageResult;
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null){
            //验证是否是当前userid下的订单，否则报错
            //订单状态刷新
            if(orderMapper.closeOrder(Collections.singletonList(order.getOrderId()),OrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0){
                return ServiceResultEnum.SUCCESS.getResult();
            }else{
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null){
            //验证是否是当前userid下的订单，否则报错
            //订单状态刷新
            order.setOrderStatus((byte) OrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            order.setUpdateTime(new Date());
            if(orderMapper.updateByPrimaryKeySelective(order)>0){
                return ServiceResultEnum.SUCCESS.getResult();
            }else{
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }
}
