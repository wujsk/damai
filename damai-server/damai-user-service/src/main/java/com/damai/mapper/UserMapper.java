package com.damai.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.damai.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: haonan
 * @description: 用户 mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
