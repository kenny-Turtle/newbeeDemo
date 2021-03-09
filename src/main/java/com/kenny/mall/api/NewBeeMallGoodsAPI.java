package com.kenny.mall.api;

import com.kenny.mall.api.vo.NewBeeMallGoodsDetailVO;
import com.kenny.mall.api.vo.NewBeeMallSearchGoodsVO;
import com.kenny.mall.common.Constants;
import com.kenny.mall.common.NewBeeMallException;
import com.kenny.mall.common.ServiceResultEnum;
import com.kenny.mall.config.annotation.TokenToMallUser;
import com.kenny.mall.dao.NewBeeMallGoodsMapper;
import com.kenny.mall.entity.MallUser;
import com.kenny.mall.entity.NewBeeMallGoods;
import com.kenny.mall.entity.Test;
import com.kenny.mall.service.NewBeeMallGoodsService;
import com.kenny.mall.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "商品相关接口")
@RequestMapping("/api/v1")
public class NewBeeMallGoodsAPI {

    private static final Logger logger = LoggerFactory.getLogger(NewBeeMallGoodsAPI.class);

    @Resource
    private NewBeeMallGoodsService GoodsService;


    @GetMapping("/search")
    @ApiOperation(value = "商品搜索接口", notes = "根据关键字和分类id进行搜索")
    public Result<PageResult<List<NewBeeMallSearchGoodsVO>>> search(
            @RequestParam(required = false) @ApiParam(value = "搜索关键字") String keyword,
            @RequestParam(required = false) @ApiParam(value = "分类id") Long goodsCategoryId,
            @RequestParam(required = false) @ApiParam(value = "orderBy") String orderBy,
            @RequestParam(required = false) @ApiParam(value = "页码") Integer pageNumber,
            @TokenToMallUser MallUser loginMallUser
    ) {
        logger.info("goods search api,keyword={},goodsCategoryId={},orderBy={},pageNumber={},userId={}", keyword, goodsCategoryId, orderBy, pageNumber, loginMallUser.getUserId());
        Map params = new HashMap(4);
        if (goodsCategoryId == null && StringUtils.isEmpty(keyword)) {
            NewBeeMallException.fail("非法的搜索参数");
        }
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("goodsCategoryId", goodsCategoryId);
        params.put("page", pageNumber);
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        if (!StringUtils.isEmpty(keyword)) {
            params.put("keyword", keyword);
        }
        if (!StringUtils.isEmpty(orderBy)) {
            params.put("orderBy", orderBy);
        }
        params.put("goodsSellStatus", Constants.SELL_STATUS_UP);
        //封装商品数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(GoodsService.searchNewBeeMallGoods(pageUtil));
    }

    @GetMapping("/goods/detail/{goodsId}")
    @ApiOperation(value = "商品详情接口",notes = "传参为商品id")
    public Result<NewBeeMallGoodsDetailVO> goodsDetail(@ApiParam(value = "商品id") @PathVariable("goodsId") Long goodsId,@TokenToMallUser MallUser loginMallUser){
        logger.info("goods detail api,goodsId={},userId={}",goodsId,loginMallUser.getUserId());
        if(goodsId < 1){
            return ResultGenerator.genFailResult("参数异常");
        }
        NewBeeMallGoods goods = GoodsService.getNewBeeMallGoodsById(goodsId);
        if(goods == null){
            return ResultGenerator.genFailResult("参数异常");
        }
        if(Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()){
            NewBeeMallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        NewBeeMallGoodsDetailVO goodsDetailVO = new NewBeeMallGoodsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        goodsDetailVO.setGoodsCarouseList(goods.getGoodsCarousel().split(","));
        return ResultGenerator.genSuccessResult(goodsDetailVO);
    }
}
