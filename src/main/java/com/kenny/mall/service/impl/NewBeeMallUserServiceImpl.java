package com.kenny.mall.service.impl;

import com.kenny.mall.common.ServiceResultEnum;
import com.kenny.mall.dao.MallUserMapper;
import com.kenny.mall.dao.NewBeeMallUserTokenMapper;
import com.kenny.mall.entity.MallUser;
import com.kenny.mall.entity.MallUserToken;
import com.kenny.mall.service.NewBeeMallUserService;
import com.kenny.mall.util.NumberUtil;
import com.kenny.mall.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import java.util.Date;

@Service
public class NewBeeMallUserServiceImpl implements NewBeeMallUserService {

    @Autowired
    private MallUserMapper mallUserMapper;

    @Autowired
    private NewBeeMallUserTokenMapper newBeeMallUserTokenMapper;

    //获取token值
    private String getNewToken(String timeStr,Long userId){
        String src = timeStr + userId + NumberUtil.genRandomNum(4);
        return SystemUtil.genToken(src);
    }

    @Override
    public String login(String loginName, String passwordMd5) {
        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMd5);
        if(user != null){
            if(user.getLockedFlag() == 1){
                return ServiceResultEnum.LOGIN_USER_LOCKED_ERROR.getResult();
            }
            //登陆后即执行修改token的操作
            String token = getNewToken(System.currentTimeMillis()+"",user.getUserId());
            MallUserToken mallUserToken = newBeeMallUserTokenMapper.selectByPrimaryKey(user.getUserId());
            //当前时间
            Date now = new Date();
            //过期时间 往后48小时
            Date expireTime = new Date(now.getTime()+2*24*3600*1000);
            if(mallUserToken == null){
                mallUserToken = new MallUserToken();
                mallUserToken.setUserId(user.getUserId());
                mallUserToken.setToken(token);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);
                //新增一条token数据
                if(newBeeMallUserTokenMapper.insertSelective(mallUserToken)>0){
                    return token;
                }
            }else{
                mallUserToken.setToken(token);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);
                if(newBeeMallUserTokenMapper.updateByPrimaryKeySelective(mallUserToken)>0){
                    return token;
                }
            }

        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }
}
