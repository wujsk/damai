package com.damai.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.damai.dto.*;
import com.damai.entity.User;
import com.damai.vo.UserGetAndTicketUserListVo;
import com.damai.vo.UserLoginVo;
import com.damai.vo.UserVo;
import jakarta.validation.Valid;

import java.util.List;

/**
 * @author: haonan
 * @description: 用户 service
 */
public interface UserService extends IService<User> {

    /**
     * 根据手机号查询用户
     * @param userMobileDto
     * @return
     */
    UserVo getByMobile(@Valid UserMobileDto userMobileDto);

    /**
     * 根据用户id查询用户
     * @param userIdDto
     * @return
     */
    UserVo getByUserId(@Valid UserIdDto userIdDto);

    /**
     * 用户注册
     * @param userRegisterDto
     * @return
     */
    Boolean register(@Valid UserRegisterDto userRegisterDto);

    /**
     * 判断用户是否存在
     * @param userExistDto
     */
    void exist(@Valid UserExistDto userExistDto);

    /**
     * 判断用户是否存在
     * @param mobile
     */
    void doExist(String mobile);

    /**
     * 用户登录
     * @param userLoginDto
     * @return
     */
    UserLoginVo login(@Valid UserLoginDto userLoginDto);

    /**
     * 用户退出登录
     * @param userLogoutDto
     * @return
     */
    Boolean logout(@Valid UserLogoutDto userLogoutDto);

    /**
     * 修改用户信息
     * @param userUpdateDto
     */
    void modify(@Valid UserUpdateDto userUpdateDto);

    /**
     * 修改用户密码
     * @param userUpdatePasswordDto
     */
    void updatePassword(@Valid UserUpdatePasswordDto userUpdatePasswordDto);

    /**
     * 修改用户邮箱
     * @param userUpdateEmailDto
     */
    void updateEmail(@Valid UserUpdateEmailDto userUpdateEmailDto);

    /**
     * 修改用户手机号
     * @param userUpdateMobileDto
     */
    void updateMobile(@Valid UserUpdateMobileDto userUpdateMobileDto);

    /**
     * 实名认证
     * @param userAuthenticationDto
     */
    void authentication(@Valid UserAuthenticationDto userAuthenticationDto);

    /**
     * 查询用户和购票人集合(只提供给服务内部调用，不提供给前端)
     * @param userGetAndTicketUserListDto
     * @return
     */
    UserGetAndTicketUserListVo getUserAndTicketUserList(@Valid UserGetAndTicketUserListDto userGetAndTicketUserListDto);

    /**
     * 查询所有手机号
     * @return
     */
    public List<String> getAllMobile();
}
