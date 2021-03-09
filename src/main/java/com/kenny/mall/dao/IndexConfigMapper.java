package com.kenny.mall.dao;

import com.kenny.mall.entity.IndexConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IndexConfigMapper {

    List<IndexConfig> findIndexConfigsByTypeAndNum(@Param("configType") int configType,@Param("number") int number);
}
