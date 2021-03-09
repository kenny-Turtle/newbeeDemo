package com.kenny.mall.api;

import com.kenny.mall.api.vo.IndexInfoVO;
import com.kenny.mall.api.vo.NewBeeMallIndexCarouselVO;
import com.kenny.mall.api.vo.NewBeeMallIndexConfigGoodsVO;
import com.kenny.mall.common.Constants;
import com.kenny.mall.common.IndexConfigTypeEnum;
import com.kenny.mall.service.NewBeeMallCarouselService;
import com.kenny.mall.service.NewBeeMallIndexConfigService;
import com.kenny.mall.util.Result;
import com.kenny.mall.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(value = "v1",tags = "新蜂商城首页接口")
@RequestMapping("/api/v1")
public class NewBeeMallIndexAPI {
    @Resource
    private NewBeeMallCarouselService newBeeMallCarouselService;

    @Resource
    private NewBeeMallIndexConfigService newBeeMallIndexConfigService;

    @GetMapping("/index-infos")
    @ApiOperation(value = "获取首页数据",notes = "轮播图、新品、推荐等")
    public Result<IndexInfoVO> indexInfo(){
        IndexInfoVO indexInfoVO = new IndexInfoVO();
        //获取轮播图数据
        List<NewBeeMallIndexCarouselVO> carousels = newBeeMallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<NewBeeMallIndexConfigGoodsVO> hotGoodses = newBeeMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<NewBeeMallIndexConfigGoodsVO> newGoodses = newBeeMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<NewBeeMallIndexConfigGoodsVO> recommendGoodses = newBeeMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        indexInfoVO.setCarousels(carousels);
        indexInfoVO.setHotGoodses(hotGoodses);
        indexInfoVO.setNewGoodses(newGoodses);
        indexInfoVO.setRecommendGoodses(recommendGoodses);
        return ResultGenerator.genSuccessResult(indexInfoVO);
    }
}
