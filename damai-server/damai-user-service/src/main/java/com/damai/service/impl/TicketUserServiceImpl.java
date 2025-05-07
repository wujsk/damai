package com.damai.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.damai.constant.RedisTimeManager;
import com.damai.core.RedisKeyManage;
import com.damai.dto.TicketUserDto;
import com.damai.dto.TicketUserIdDto;
import com.damai.dto.TicketUserListDto;
import com.damai.entity.TicketUser;
import com.damai.entity.User;
import com.damai.enums.BaseCode;
import com.damai.exception.DaMaiFrameException;
import com.damai.mapper.TicketUserMapper;
import com.damai.mapper.UserMapper;
import com.damai.redis.RedisCache;
import com.damai.redis.RedisKeyBuild;
import com.damai.service.TicketUserService;
import com.damai.servicelock.LockType;
import com.damai.servicelock.ServiceLocker;
import com.damai.servicelock.factory.ServiceLockFactory;
import com.damai.vo.TicketUserVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author: haonan
 * @description: 购票人 service impl
 */
@Service
public class TicketUserServiceImpl extends ServiceImpl<TicketUserMapper, TicketUser> implements TicketUserService {

    @Resource
    private RedisCache redisCache;

    @Resource
    private ServiceLockFactory serviceLockFactory;

    @Resource
    private UidGenerator uidGenerator;

    @Resource
    private UserMapper userMapper;

    @Override
    public void add(TicketUserDto ticketUserDto) {
        User user = userMapper.selectById(ticketUserDto.getUserId());
        if (user == null) {
            throw new DaMaiFrameException(BaseCode.USER_EMPTY);
        }
        LambdaQueryWrapper<TicketUser> queryWrapper = Wrappers.lambdaQuery(TicketUser.class)
                .eq(TicketUser::getUserId, ticketUserDto.getUserId())
                .eq(TicketUser::getIdType, ticketUserDto.getIdType())
                .eq(TicketUser::getIdNumber, ticketUserDto.getIdNumber());
        TicketUser ticketUser = getOne(queryWrapper);
        if (ticketUser != null) {
            throw new DaMaiFrameException(BaseCode.TICKET_USER_EXIST);
        }
        ticketUser = BeanUtil.toBean(ticketUserDto, TicketUser.class);
        ticketUser.setId(uidGenerator.getUid());
        save(ticketUser);
        delTicketUserVoListCache(String.valueOf(ticketUserDto.getUserId()));
    }

    @Override
    public void delete(TicketUserIdDto ticketUserIdDto) {
        TicketUser ticketUser = getById(ticketUserIdDto.getId());
        if (ticketUser == null) {
            throw new DaMaiFrameException(BaseCode.TICKET_USER_EMPTY);
        }
        removeById(ticketUserIdDto.getId());
        delTicketUserVoListCache(String.valueOf(ticketUser.getUserId()));
    }

    private void delTicketUserVoListCache(String userId) {
        redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.TICKET_USER_LIST, userId));
    }

    @Override
    public List<TicketUserVo> listTicketUser(TicketUserListDto ticketUserListDto) {
        //先从缓存中查询
        List<TicketUserVo> ticketUserVoList = redisCache.getValueIsList(RedisKeyBuild.createRedisKey(
                RedisKeyManage.TICKET_USER_LIST, ticketUserListDto.getUserId()), TicketUserVo.class);
        if (CollUtil.isNotEmpty(ticketUserVoList)) {
            return ticketUserVoList;
        }
        ServiceLocker lock = serviceLockFactory.getLock(LockType.Reentrant);
        RedisKeyBuild keyBuild = RedisKeyBuild.createRedisKey(RedisKeyManage.TICKET_USER_LIST_LOCK, ticketUserListDto.getUserId());
        lock.lock(keyBuild.getRelKey(), RedisTimeManager.TICKET_USER_LIST_LOCK_TIME);
        try {
            LambdaQueryWrapper<TicketUser> queryWrapper = Wrappers.lambdaQuery(TicketUser.class)
                    .eq(TicketUser::getUserId, ticketUserListDto.getUserId());
            List<TicketUser> ticketUsers = list(queryWrapper);
            if (CollUtil.isEmpty(ticketUsers)) {
                return Collections.emptyList();
            }
            List<TicketUserVo> ticketUserVos = BeanUtil.copyToList(ticketUsers, TicketUserVo.class);
            redisCache.set(RedisKeyBuild.createRedisKey(RedisKeyManage.TICKET_USER_LIST, ticketUserListDto.getUserId()),
                    ticketUserVos, RedisTimeManager.TICKET_USER_LIST_CACHE_TIME, TimeUnit.DAYS);
            return ticketUserVos;
        } finally {
            lock.unlock(keyBuild.getRelKey());
        }
    }
}
