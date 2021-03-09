package com.kenny.mall.dao;

import com.kenny.mall.entity.MallUser;
import org.apache.ibatis.annotations.Param;

public interface MallUserMapper {


    MallUser selectByLoginNameAndPasswd(@Param("loginName") String loginName, @Param("password") String password);

    MallUser selectByPrimaryKey(Long userId);
}
