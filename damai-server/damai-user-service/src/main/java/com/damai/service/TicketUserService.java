package com.damai.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.damai.dto.TicketUserDto;
import com.damai.dto.TicketUserIdDto;
import com.damai.dto.TicketUserListDto;
import com.damai.entity.TicketUser;
import com.damai.vo.TicketUserVo;
import jakarta.validation.Valid;

import java.util.List;

/**
 * @author: haonan
 * @description: 购票人 service
 */
public interface TicketUserService extends IService<TicketUser> {

    /**
     * 添加购票人
     * @param ticketUserDto
     */
    void add(@Valid TicketUserDto ticketUserDto);

    /**
     * 删除购票人
     * @param ticketUserIdDto
     */
    void delete(@Valid TicketUserIdDto ticketUserIdDto);

    /**
     * 查询购票人列表
     * @param ticketUserListDto
     * @return
     */
    List<TicketUserVo> listTicketUser(@Valid TicketUserListDto ticketUserListDto);
}
