package com.damai.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.damai.core.RedisKeyManage;
import com.damai.dto.ChannelDataAddDto;
import com.damai.dto.GetChannelDataByCodeDto;
import com.damai.entity.ChannelTableData;
import com.damai.enums.Status;
import com.damai.mapper.ChannelDataMapper;
import com.damai.redis.RedisCache;
import com.damai.redis.RedisKeyBuild;
import com.damai.service.ChannelDataService;
import com.damai.util.DateUtils;
import com.damai.vo.GetChannelDataVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author: haonan
 * @description: 渠道 service impl
 */
@Service
public class ChannelDataServiceImpl extends ServiceImpl<ChannelDataMapper, ChannelTableData> implements ChannelDataService {

    @Resource
    private UidGenerator uidGenerator;

    @Resource
    private RedisCache redisCache;

    @Override
    public void add(ChannelDataAddDto channelDataAddDto) {
        ChannelTableData channelData = new ChannelTableData();
        BeanUtil.copyProperties(channelDataAddDto,channelData);
        channelData.setId(uidGenerator.getUid());
        channelData.setCreateTime(DateUtils.now());
        save(channelData);
        addRedisChannelData(channelData);
    }

    private void addRedisChannelData(ChannelTableData channelData){
        GetChannelDataVo getChannelDataVo = new GetChannelDataVo();
        BeanUtil.copyProperties(channelData,getChannelDataVo);
        redisCache.set(RedisKeyBuild.createRedisKey(RedisKeyManage.CHANNEL_DATA,getChannelDataVo.getCode()),getChannelDataVo);
    }

    @Override
    public GetChannelDataVo getByCode(GetChannelDataByCodeDto getChannelDataByCodeDto) {
        LambdaQueryWrapper<ChannelTableData> queryWrapper = Wrappers.lambdaQuery(ChannelTableData.class)
                .eq(ChannelTableData::getStatus, Status.RUN.getCode())
                .eq(ChannelTableData::getCode, getChannelDataByCodeDto.getCode());
        ChannelTableData channelTableData = getOne(queryWrapper);
        if (null == channelTableData) {
            return null;
        }
        return BeanUtil.toBean(channelTableData, GetChannelDataVo.class);
    }
}
