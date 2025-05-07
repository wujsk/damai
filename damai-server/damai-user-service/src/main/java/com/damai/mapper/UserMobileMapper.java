package com.damai.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.damai.entity.UserMobile;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: haonan
 * @description: 用户电话关联 mapper
 */
@Mapper
public interface UserMobileMapper extends BaseMapper<UserMobile> {
}
