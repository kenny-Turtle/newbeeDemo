package com.kenny.mall.api;

import com.kenny.mall.api.param.SaveUserAddressParam;
import com.kenny.mall.api.param.UpdateUserAddressParam;
import com.kenny.mall.api.vo.UserAddressVO;
import com.kenny.mall.common.ServiceResultEnum;
import com.kenny.mall.config.annotation.TokenToMallUser;
import com.kenny.mall.dao.MallUserMapper;
import com.kenny.mall.entity.MallUser;
import com.kenny.mall.entity.UserAddress;
import com.kenny.mall.service.UserAddressService;
import com.kenny.mall.util.BeanUtil;
import com.kenny.mall.util.Result;
import com.kenny.mall.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author zfj
 * @create 2021/3/15 11:24
 */
@RestController
@Api(value = "v1", tags = "个人地址相关接口")
@RequestMapping("/api/v1")
public class UserAddressAPI {

    @Resource
    private UserAddressService userAddressService;

    @GetMapping("/address")
    @ApiOperation(value = "我的收货地址列表",notes = "")
    public Result<List<UserAddressVO>> addressList(@TokenToMallUser MallUser mallUser){
        return ResultGenerator.genSuccessResult(userAddressService.getMyAddress(mallUser.getUserId()));
    }

    @PostMapping("/address")
    @ApiOperation(value = "添加地址" , notes = "")
    public Result<Boolean> saveUserAddress(@RequestBody SaveUserAddressParam saveUserAddressParam,
                                           @TokenToMallUser MallUser mallUser){
        UserAddress userAddress = new UserAddress();
        BeanUtil.copyProperties(saveUserAddressParam,userAddress);
        userAddress.setUserId(mallUser.getUserId());
        Boolean saveResult = userAddressService.saveUserAddress(userAddress);
        //添加成功
        if(saveResult){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult("添加失败");
        }
    }
    @PutMapping("/address")
    @ApiOperation(value = "修改地址",notes = "")
    public Result<Boolean> updateUserAddress(@RequestBody UpdateUserAddressParam updateUserAddressParam,
                                             @TokenToMallUser MallUser mallUser){
        UserAddress userAddressById = userAddressService.getUserAddressById(updateUserAddressParam.getAddressId());
        if(!userAddressById.getUserId().equals(mallUser.getUserId())){
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        UserAddress userAddress = new UserAddress();
        BeanUtil.copyProperties(updateUserAddressParam,userAddress);
        userAddress.setUserId(mallUser.getUserId());
        Boolean updateResult = userAddressService.updateUserAddress(userAddress);
        if(updateResult){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    @GetMapping("/address/{addressId}")
    @ApiOperation(value = "获取收货地址详情",notes = "")
    public Result<UserAddressVO> getUserAddress(@PathVariable("addressId") Long addressId,
                                                @TokenToMallUser MallUser mallUser){
        UserAddress userAddressById = userAddressService.getUserAddressById(addressId);
        UserAddressVO userAddressVO = new UserAddressVO();
        BeanUtil.copyProperties(userAddressById,userAddressVO);
        if(!userAddressVO.getUserId().equals(mallUser.getUserId())){
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        return ResultGenerator.genSuccessResult(userAddressVO);
    }

    @GetMapping("/address/{default}")
    @ApiOperation(value = "获取默认地址",notes = "")
    public Result getDefaultUserAddress(@TokenToMallUser MallUser mallUser){
        UserAddress myDefaultAddressByUserId = userAddressService.getMyDefaultAddressByUserId(mallUser.getUserId());
        return ResultGenerator.genSuccessResult(myDefaultAddressByUserId);
    }

    @DeleteMapping("/address/{addressId}")
    @ApiOperation(value = "删除收货地址",notes = "")
    public Result deleteAddress(@PathVariable("addressId")Long addressId,
                                @TokenToMallUser MallUser mallUser){
        UserAddress userAddressById = userAddressService.getUserAddressById(addressId);
        if(!mallUser.getUserId().equals(userAddressById.getUserId())){
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        Boolean result = userAddressService.deletedById(userAddressById.getAddressId());
        if(result){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
        }
    }
}
