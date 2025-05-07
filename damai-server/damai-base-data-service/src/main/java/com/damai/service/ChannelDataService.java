package com.damai.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.damai.dto.ChannelDataAddDto;
import com.damai.dto.GetChannelDataByCodeDto;
import com.damai.entity.ChannelTableData;
import com.damai.vo.GetChannelDataVo;
import jakarta.validation.Valid;

/**
 * @author: haonan
 * @description: 渠道 service
 */
public interface ChannelDataService extends IService<ChannelTableData> {

    /**
     * 根据code获取渠道数据
     * @param getChannelDataByCodeDto
     * @return
     */
    GetChannelDataVo getByCode(@Valid GetChannelDataByCodeDto getChannelDataByCodeDto);

    /**
     * 新增渠道数据
     * @param channelDataAddDto
     */
    void add(@Valid ChannelDataAddDto channelDataAddDto);
}
