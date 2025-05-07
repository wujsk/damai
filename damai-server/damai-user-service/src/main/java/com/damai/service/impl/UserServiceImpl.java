package com.damai.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.damai.annotion.ServiceLock;
import com.damai.client.BaseDataClient;
import com.damai.common.ApiResponse;
import com.damai.core.RedisKeyManage;
import com.damai.dto.*;
import com.damai.entity.TicketUser;
import com.damai.entity.User;
import com.damai.entity.UserEmail;
import com.damai.entity.UserMobile;
import com.damai.enums.BaseCode;
import com.damai.enums.BusinessStatus;
import com.damai.enums.CompositeCheckType;
import com.damai.exception.DaMaiFrameException;
import com.damai.handler.BloomFilterHandler;
import com.damai.initialize.impl.composite.CompositeContainer;
import com.damai.jwt.TokenUtil;
import com.damai.mapper.TicketUserMapper;
import com.damai.mapper.UserEmailMapper;
import com.damai.mapper.UserMapper;
import com.damai.mapper.UserMobileMapper;
import com.damai.redis.RedisCache;
import com.damai.redis.RedisKeyBuild;
import com.damai.service.UserService;
import com.damai.servicelock.LockType;
import com.damai.vo.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.damai.core.DistributedLockConstants.REGISTER_USER_LOCK;

/**
 * @author: haonan
 * @description: 用户 service impl
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMobileMapper userMobileMapper;

    @Resource
    private UserEmailMapper userEmailMapper;

    @Resource
    private BloomFilterHandler bloomFilterHandler;

    @Resource
    private CompositeContainer compositeContainer;

    @Resource
    private UidGenerator uidGenerator;

    @Resource
    private RedisCache redisCache;

    @Resource
    private BaseDataClient baseDataClient;

    @Resource
    private TicketUserMapper ticketUserMapper;

    @Value("${token.expire.time:40}")
    private Long tokenExpireTime;

    private static final Integer ERROR_COUNT_THRESHOLD = 5;

    @Override
    public UserVo getByMobile(UserMobileDto userMobileDto) {
        LambdaQueryWrapper<UserMobile> queryWrapper = Wrappers.lambdaQuery(UserMobile.class)
                .eq(UserMobile::getMobile, userMobileDto.getMobile());
        UserMobile userMobile = userMobileMapper.selectOne(queryWrapper);
        if (Objects.isNull(userMobile)) {
            throw new DaMaiFrameException(BaseCode.USER_MOBILE_EMPTY);
        }
        User user = getById(userMobile.getUserId());
        if (Objects.isNull(user)) {
            throw new DaMaiFrameException(BaseCode.USER_EMPTY);
        }
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user,userVo);
        userVo.setMobile(userMobile.getMobile());
        return userVo;
    }

    @Override
    public UserVo getByUserId(UserIdDto userIdDto) {
        User user = getById(userIdDto.getId());
        if (Objects.isNull(user)) {
            throw new DaMaiFrameException(BaseCode.USER_EMPTY);
        }
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user,userVo);
        return userVo;
    }

    @Override
    @ServiceLock(lockType = LockType.Write, name = REGISTER_USER_LOCK, keys = {"#userRegisterDto.mobile)"})
    @Transactional(rollbackFor = Exception.class)
    public Boolean register(UserRegisterDto userRegisterDto) {
        compositeContainer.execute(CompositeCheckType.USER_REGISTER_CHECK.getValue(),userRegisterDto);
        //用户表添加
        User user = new User();
        BeanUtils.copyProperties(userRegisterDto,user);
        user.setId(uidGenerator.getUid());
        save(user);
        // 用户手机号表添加
        UserMobile userMobile = new UserMobile();
        userMobile.setId(uidGenerator.getUid());
        userMobile.setUserId(user.getId());
        userMobile.setMobile(userRegisterDto.getMobile());
        userMobileMapper.insert(userMobile);
        bloomFilterHandler.add(userMobile.getMobile());
        return true;
    }

    @Override
    @ServiceLock(lockType = LockType.Read, name = REGISTER_USER_LOCK, keys = {"#userExistDto.mobile"})
    public void exist(UserExistDto userExistDto) {
        doExist(userExistDto.getMobile());
    }

    @Override
    public void doExist(String mobile) {
        if (bloomFilterHandler.contains(mobile)) {
            LambdaQueryWrapper<UserMobile> queryWrapper = Wrappers.lambdaQuery(UserMobile.class)
                    .eq(UserMobile::getMobile, mobile);
            UserMobile userMobile = userMobileMapper.selectOne(queryWrapper);
            if (Objects.nonNull(userMobile)) {
                throw new DaMaiFrameException(BaseCode.USER_EXIST);
            }
        }
    }

    @Override
    public UserLoginVo login(UserLoginDto userLoginDto) {
        // 1. 验证邮箱和手机号至少有一个不为空
        this.verifyEmailAndMobile(userLoginDto);
        // 2. 根据邮箱或手机号查询用户id
        Long userId = this.getUserIdByEmailOrMobile(userLoginDto);
        // 3. 根据用户id查询用户信息
        User user = this.getUserById(userId);
        // 4. 校验密码是否正确
        this.verifyPassword(userLoginDto, user);
        // 5. 保存用户登录信息
        this.saveLoginInfoToRedis(userLoginDto.getCode(), user);
        // 6. 返回封装结果
        return UserLoginVo.builder()
                .userId(userId).token(createToken(user.getId(),getChannelDataByCode(userLoginDto.getCode()).getTokenSecret()))
                .build();
    }

    private GetChannelDataVo getChannelDataByClient(String code){
        GetChannelDataByCodeDto getChannelDataByCodeDto = new GetChannelDataByCodeDto();
        getChannelDataByCodeDto.setCode(code);
        ApiResponse<GetChannelDataVo> getChannelDataApiResponse = baseDataClient.getByCode(getChannelDataByCodeDto);
        if (Objects.equals(getChannelDataApiResponse.getCode(), BaseCode.SUCCESS.getCode())) {
            return getChannelDataApiResponse.getData();
        }
        throw new DaMaiFrameException("没有找到ChannelData");
    }

    private GetChannelDataVo getChannelDataByRedis(String code){
        return redisCache.get(RedisKeyBuild.createRedisKey(RedisKeyManage.CHANNEL_DATA,code),GetChannelDataVo.class);
    }

    public GetChannelDataVo getChannelDataByCode(String code){
        GetChannelDataVo channelData = getChannelDataByRedis(code);
        if (Objects.isNull(channelData)) {
            channelData = getChannelDataByClient(code);
        }
        return channelData;
    }

    public String createToken(Long userId,String tokenSecret){
        Map<String,Object> map = new HashMap<>(4);
        map.put("userId",userId);
        return TokenUtil.createToken(String.valueOf(uidGenerator.getUid()), JSON.toJSONString(map),tokenExpireTime * 60 * 1000, tokenSecret);
    }

    private void saveLoginInfoToRedis(String code, User user) {
        redisCache.set(RedisKeyBuild.createRedisKey(RedisKeyManage.USER_LOGIN,code,user.getId()),user,
                tokenExpireTime,TimeUnit.MINUTES);
    }

    private void verifyPassword(UserLoginDto userLoginDto, User user) {
        String passwordDb = user.getPassword();
        String password = userLoginDto.getPassword();
        if (!password.equals(passwordDb)) {
            throw new DaMaiFrameException(BaseCode.NAME_PASSWORD_ERROR);
        }
    }

    private User getUserById(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new DaMaiFrameException(BaseCode.NAME_PASSWORD_ERROR);
        }
        return user;
    }

    private Long getUserIdByEmailOrMobile(UserLoginDto userLoginDto) {
        String email = userLoginDto.getEmail();
        String mobile = userLoginDto.getMobile();
        if (StrUtil.isNotBlank(email)) {
            limitErrorCount(RedisKeyManage.LOGIN_USER_EMAIL_ERROR, email, BaseCode.EMAIL_ERROR_COUNT_TOO_MANY);
            LambdaQueryWrapper<UserEmail> queryWrapper = Wrappers.lambdaQuery(UserEmail.class)
                    .eq(UserEmail::getEmail, userLoginDto.getEmail());
            UserEmail userEmail = userEmailMapper.selectOne(queryWrapper);
            if (Objects.isNull(userEmail)) {
                incrErrorCountAndThrow(RedisKeyManage.LOGIN_USER_EMAIL_ERROR, email, BaseCode.EMAIL_ERROR_COUNT_TOO_MANY);
            }
            return userEmail.getUserId();
        } else {
            limitErrorCount(RedisKeyManage.LOGIN_USER_MOBILE_ERROR, mobile, BaseCode.MOBILE_ERROR_COUNT_TOO_MANY);
            LambdaQueryWrapper<UserMobile> queryWrapper = Wrappers.lambdaQuery(UserMobile.class)
                    .eq(UserMobile::getMobile, userLoginDto.getMobile());
            UserMobile userMobile = userMobileMapper.selectOne(queryWrapper);
            if (Objects.isNull(userMobile)) {
                incrErrorCountAndThrow(RedisKeyManage.LOGIN_USER_MOBILE_ERROR, mobile, BaseCode.MOBILE_ERROR_COUNT_TOO_MANY);
            }
            return userMobile.getUserId();
        }
    }

    private void incrErrorCountAndThrow(RedisKeyManage redisKeyManage, String key, BaseCode baseCode) {
        redisCache.incrBy(RedisKeyBuild.createRedisKey(redisKeyManage, key),1);
        redisCache.expire(RedisKeyBuild.createRedisKey(redisKeyManage, key), 1, TimeUnit.MINUTES);
        throw new DaMaiFrameException(baseCode);
    }

    private void limitErrorCount(RedisKeyManage redisKeyManage, String key, BaseCode baseCode) {
        String errorCountStr =
                redisCache.get(RedisKeyBuild.createRedisKey(redisKeyManage, key), String.class);
        if (StrUtil.isNotBlank(errorCountStr) && Integer.parseInt(errorCountStr) >= ERROR_COUNT_THRESHOLD) {
            throw new DaMaiFrameException(baseCode);
        }
    }

    private void verifyEmailAndMobile(UserLoginDto userLoginDto) {
        if (StrUtil.isBlank(userLoginDto.getEmail()) && StrUtil.isBlank(userLoginDto.getMobile())) {
            throw new DaMaiFrameException(BaseCode.USER_MOBILE_AND_EMAIL_NOT_EXIST);
        }
    }

    @Override
    public Boolean logout(UserLogoutDto userLogoutDto) {
        String code = userLogoutDto.getCode();
        String token = userLogoutDto.getToken();
        String userStr = TokenUtil.parseToken(token, getChannelDataByCode(code).getTokenSecret());
        if (StrUtil.isBlank(userStr)) {
            throw new DaMaiFrameException(BaseCode.USER_EMPTY);
        }
        String userId = JSONObject.parseObject(userStr).getString("userId");
        redisCache.del(RedisKeyBuild.createRedisKey(RedisKeyManage.USER_LOGIN,code,userId));
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modify(UserUpdateDto userUpdateDto) {
        // 1. 校验用户是否存在
        User user = getById(userUpdateDto.getId());
        if (user == null) {
            throw new DaMaiFrameException(BaseCode.USER_EMPTY);
        }
        // 2.更新用户信息
        modifyUserInfo(userUpdateDto);
        // 3.更新\插入手机号
        insertOrUpdateMobile(userUpdateDto.getMobile(), user);
    }

    public void modifyUserInfo(UserUpdateDto userUpdateDto) {
        User updateUser = new User();
        BeanUtil.copyProperties(userUpdateDto,updateUser);
        updateById(updateUser);
    }

    public void insertOrUpdateMobile(String mobile, User user) {
        if (StrUtil.isNotBlank(mobile)) {
            UserMobile userMobile = userMobileMapper.selectOne(Wrappers.lambdaQuery(UserMobile.class)
                    .eq(UserMobile::getUserId, user.getId()));
            if (Objects.isNull(userMobile)) {
                userMobile = new UserMobile();
                userMobile.setId(uidGenerator.getUid());
                userMobile.setUserId(user.getId());
                userMobile.setMobile(mobile);
                userMobileMapper.insert(userMobile);
            } else {
                if (userMobile.getMobile().equals(mobile)) {
                    return;
                }
                UserMobile updateUserMobile = new UserMobile();
                updateUserMobile.setId(userMobile.getId());
                updateUserMobile.setMobile(mobile);
                userMobileMapper.updateById(userMobile);
            }
        }
    }

    public void insertOrUpdateEmail(String email, User user) {
        if (StrUtil.isNotBlank(email)) {
            UserEmail userEmail = userEmailMapper.selectOne(Wrappers.lambdaQuery(UserEmail.class)
                    .eq(UserEmail::getUserId, user.getId()));
            if (Objects.isNull(userEmail)) {
                userEmail = new UserEmail();
                userEmail.setId(uidGenerator.getUid());
                userEmail.setUserId(user.getId());
                userEmail.setEmail(email);
                userEmailMapper.insert(userEmail);
            } else {
                if (userEmail.getEmail().equals(email)) {
                    return;
                }
                UserEmail updateUserEmail = new UserEmail();
                updateUserEmail.setId(userEmail.getId());
                updateUserEmail.setEmail(email);
                userEmailMapper.updateById(userEmail);
            }
        }
    }

    @Override
    public void updatePassword(UserUpdatePasswordDto userUpdatePasswordDto) {
        User user = getById(userUpdatePasswordDto.getId());
        if (user == null) {
            throw new DaMaiFrameException(BaseCode.USER_EMPTY);
        }
        if (user.getPassword().equals(userUpdatePasswordDto.getPassword())) {
            return;
        }
        updateById(BeanUtil.toBean(userUpdatePasswordDto, User.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(UserUpdateEmailDto userUpdateEmailDto) {
        User user = getById(userUpdateEmailDto.getId());
        if (user == null) {
            throw new DaMaiFrameException(BaseCode.USER_EMPTY);
        }
        if (!user.getEmail().equals(userUpdateEmailDto.getEmail())) {
            User updateUser = new User();
            updateUser.setId(user.getId());
            updateUser.setEmail(userUpdateEmailDto.getEmail());
            updateById(updateUser);
            insertOrUpdateEmail(userUpdateEmailDto.getEmail(), user);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMobile(UserUpdateMobileDto userUpdateMobileDto) {
        User user = getById(userUpdateMobileDto.getId());
        if (user == null) {
            throw new DaMaiFrameException(BaseCode.USER_EMPTY);
        }
        if (!user.getMobile().equals(userUpdateMobileDto.getMobile())) {
            User updateUser = new User();
            updateUser.setId(user.getId());
            updateUser.setMobile(userUpdateMobileDto.getMobile());
            updateById(updateUser);
            insertOrUpdateMobile(userUpdateMobileDto.getMobile(), user);
        }
    }

    @Override
    public void authentication(UserAuthenticationDto userAuthenticationDto) {
        User user = getById(userAuthenticationDto.getId());
        if (Objects.isNull(user)) {
            throw new DaMaiFrameException(BaseCode.USER_EMPTY);
        }
        if (Objects.equals(user.getRelAuthenticationStatus(), BusinessStatus.YES.getCode())) {
            throw new DaMaiFrameException(BaseCode.USER_AUTHENTICATION);
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setRelName(userAuthenticationDto.getRelName());
        updateUser.setIdNumber(userAuthenticationDto.getIdNumber());
        updateUser.setRelAuthenticationStatus(BusinessStatus.YES.getCode());
        updateById(updateUser);
    }

    @Override
    public UserGetAndTicketUserListVo getUserAndTicketUserList(UserGetAndTicketUserListDto userGetAndTicketUserListDto) {
        UserIdDto userIdDto = new UserIdDto();
        userIdDto.setId(userGetAndTicketUserListDto.getUserId());
        UserVo userVo = getByUserId(userIdDto);

        List<TicketUser> ticketUsers = ticketUserMapper.selectList(Wrappers.lambdaQuery(TicketUser.class)
                .eq(TicketUser::getUserId, userGetAndTicketUserListDto.getUserId()));
        List<TicketUserVo> ticketUserVos = BeanUtil.copyToList(ticketUsers, TicketUserVo.class);
        return UserGetAndTicketUserListVo.builder()
                .userVo(userVo)
                .ticketUserVoList(ticketUserVos)
                .build();
    }

    @Override
    public List<String> getAllMobile() {
        QueryWrapper<User> lambdaQueryWrapper = Wrappers.emptyWrapper();
        List<User> users = list(lambdaQueryWrapper);
        return users.stream().map(User::getMobile).collect(Collectors.toList());
    }
}
