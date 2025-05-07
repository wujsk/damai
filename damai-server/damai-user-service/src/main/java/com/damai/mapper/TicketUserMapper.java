package com.damai.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.damai.entity.TicketUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: haonan
 * @description: 用户购票 mapper
 */
@Mapper
public interface TicketUserMapper extends BaseMapper<TicketUser> {
}
