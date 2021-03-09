package com.kenny.mall.api;

import com.kenny.mall.api.param.MallUserLoginParam;
import com.kenny.mall.api.vo.NewBeeMallUserVO;
import com.kenny.mall.common.Constants;
import com.kenny.mall.config.annotation.TokenToMallUser;
import com.kenny.mall.entity.MallUser;
import com.kenny.mall.service.NewBeeMallUserService;
import com.kenny.mall.util.BeanUtil;
import com.kenny.mall.util.Result;
import com.kenny.mall.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Api(value = "v1",tags = "demo商城用户操作相关接口")
@RequestMapping("/api/v1")
public class NewBeeMallPersonalAPI {

    @Resource
    private NewBeeMallUserService newBeeMallUserService;

    private static final Logger logger = LoggerFactory.getLogger(NewBeeMallPersonalAPI.class);

    @PostMapping("/user/login")
    @ApiOperation(value = "登陆接口",notes = "返回token")
    public Result<String> login(@RequestBody @Valid MallUserLoginParam mallUserLoginParam){
        System.out.println(">>>>debug here<<<<<");
        String loginResult = newBeeMallUserService.login(mallUserLoginParam.getLoginName(), mallUserLoginParam.getPasswordMd5());
        //登陆成功
        if(!StringUtils.isEmpty(loginResult)&&loginResult.length()== Constants.TOKEN_LENGTH){
            Result result = ResultGenerator.genSuccessResult();
            result.setData(loginResult);
            return result;
        }
        //登陆失败
        return ResultGenerator.genFailResult(loginResult);
    }

    @GetMapping("/user/info")
    @ApiOperation(value = "获取用户信息",notes = "")
    public Result<NewBeeMallUserVO> getUserDetail(@TokenToMallUser MallUser loginNallUser){
        //已登陆则直接返回
        System.out.println(">>>>>>>><<<<<<<<<<");
        NewBeeMallUserVO mallUserVO = new NewBeeMallUserVO();
        loginNallUser.setNickName("kenny");
        loginNallUser.setLoginName("jay");
        loginNallUser.setIntroduceSign("still DRE");
        BeanUtil.copyProperties(loginNallUser,mallUserVO);
        return ResultGenerator.genSuccessResult(mallUserVO);
    }

}
