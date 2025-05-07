package com.damai.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.damai.entity.UserEmail;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: haonan
 * @description: 用户邮箱 mapper
 */
@Mapper
public interface UserEmailMapper extends BaseMapper<UserEmail> {
}
