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
            NewBeeMallException.fail(goodsListNotSelling.get(0).getGoodsName()+"?????????????????????????????????");
        }
        Map<Long, NewBeeMallGoods> goodsMap =
                goods.stream().collect(Collectors.toMap(NewBeeMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //????????????
        for (ShoppingCartItemVO itemVO : itemVOList) {
            //?????????????????????????????????????????????id???????????????????????????
            if(!goodsMap.containsKey(itemVO.getGoodsId())){
                NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //??????????????????????????????????????????????????????????????????
            if(itemVO.getGoodsCount() > goodsMap.get(itemVO.getGoodsId()).getStockNum()){
                NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //???????????????
        if(!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(goods)){
            if(shoppingCartItemMapper.deleteBatch(itemIdList) > 0){
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(itemVOList, StockNumDTO.class);
                int stockNumResult = goodsMapper.updateStockNum(stockNumDTOS);
                if(stockNumResult < 1){
                    NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //???????????????
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //????????????
                Order order = new Order();
                order.setOrderNo(orderNo);
                order.setUserId(user.getUserId());
                //??????
                for (ShoppingCartItemVO item : itemVOList) {
                    priceTotal += item.getSellingPrice() * item.getGoodsCount();
                }
                if(priceTotal < 1){
                    NewBeeMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                order.setTotalPrice(priceTotal);
                String extraInfo = "";
                order.setExtraInfo(extraInfo);
                //???????????????????????????????????????
                if(orderMapper.insertSelective(order) > 0){
                    //??????????????????????????????????????????????????????
                    OrderAddress orderAddress = new OrderAddress();
                    BeanUtil.copyProperties(address, orderAddress);
                    orderAddress.setOrderId(order.getOrderId());
                    //???????????????????????????????????????????????????
                    List<OrderItem> orderItems = new ArrayList<>();
                    for (ShoppingCartItemVO item : itemVOList) {
                        OrderItem orderItem = new OrderItem();
                        BeanUtil.copyProperties(item,orderItem);
                        orderItem.setOrderId(order.getOrderId());
                        orderItems.add(orderItem);
                    }
                    //??????????????????
                    if(orderItemMapper.insertBatch(orderItems) > 0 && orderAddressMapper.insertSelective(orderAddress) > 0){
                        //???????????????????????????????????????????????????controller???????????????????????????
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
                NewBeeMallException.fail("???????????????????????????????????????");
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
        //?????????????????????
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
            //?????????????????????????????????vo
            orderListVOS = BeanUtil.copyList(orders,OrderListVO.class);
            // ?????????????????????????????????
            for (OrderListVO orderListVO : orderListVOS) {
                orderListVO.setOrderStatusString(OrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(orderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = orders.stream().map(Order::getOrderId).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(orderIds)){
                List<OrderItem> orderItems = orderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<OrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(OrderItem::getOrderId));
                for (OrderListVO orderListVO : orderListVOS) {
                    //??????????????????????????????????????????
                    if(itemByOrderIdMap.containsKey(orderListVO.getOrderId())){
                        List<OrderItem> orderItemTemp = itemByOrderIdMap.get(orderListVO.getOrderId());
                        //???orderItem?????????????????????orderItemVO????????????
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
            //?????????????????????userid???????????????????????????
            //??????????????????
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
            //?????????????????????userid???????????????????????????
            //??????????????????
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
