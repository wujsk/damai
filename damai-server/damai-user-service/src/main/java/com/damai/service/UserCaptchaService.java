package com.damai.service;


import com.damai.captcha.model.common.ResponseModel;
import com.damai.captcha.model.vo.CaptchaVO;
import com.damai.vo.CheckNeedCaptchaDataVo;

/**
 * @author: haonan
 * @description: 验证码 service
 */
public interface UserCaptchaService {

    /**
     * 检查是否需要验证码
     * @return
     */
    CheckNeedCaptchaDataVo checkNeedCaptcha();

    /**
     * 获取验证码
     * @param captchaVO
     * @return
     */
    ResponseModel getCaptcha(CaptchaVO captchaVO);

    /**
     * 验证验证码
     * @param captchaVO
     * @return
     */
    ResponseModel verifyCaptcha(CaptchaVO captchaVO);
}
