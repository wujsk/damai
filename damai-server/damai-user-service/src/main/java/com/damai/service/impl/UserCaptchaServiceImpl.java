package com.damai.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.damai.captcha.model.common.ResponseModel;
import com.damai.captcha.model.vo.CaptchaVO;
import com.damai.core.RedisKeyManage;
import com.damai.redis.RedisKeyBuild;
import com.damai.service.CaptchaHandle;
import com.damai.service.UserCaptchaService;
import com.damai.service.lua.CheckNeedCaptchaOperate;
import com.damai.vo.CheckNeedCaptchaDataVo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: haonan
 * @description: 验证码 service impl
 */
@Service
public class UserCaptchaServiceImpl implements UserCaptchaService {

    @Value("${verify_captcha_threshold:10}")
    private int verifyCaptchaThreshold;

    @Value("${verify_captcha_id_expire_time:60}")
    private int verifyCaptchaIdExpireTime;

    @Value("${always_verify_captcha:0}")
    private int alwaysVerifyCaptcha;

    @Resource
    private CaptchaHandle captchaHandle;

    @Resource
    private UidGenerator uidGenerator;

    @Resource
    private CheckNeedCaptchaOperate checkNeedCaptchaOperate;

    @Override
    public CheckNeedCaptchaDataVo checkNeedCaptcha() {
        long currentTimeMillis = System.currentTimeMillis();
        long id = uidGenerator.getUid();
        List<String> keys = new ArrayList<>();
        keys.add(RedisKeyBuild.createRedisKey(RedisKeyManage.COUNTER_COUNT).getRelKey());
        keys.add(RedisKeyBuild.createRedisKey(RedisKeyManage.COUNTER_TIMESTAMP).getRelKey());
        keys.add(RedisKeyBuild.createRedisKey(RedisKeyManage.VERIFY_CAPTCHA_ID,id).getRelKey());
        String[] data = new String[4];
        data[0] = String.valueOf(verifyCaptchaThreshold);
        data[1] = String.valueOf(currentTimeMillis);
        data[2] = String.valueOf(verifyCaptchaIdExpireTime);
        data[3] = String.valueOf(alwaysVerifyCaptcha);
        Boolean result = checkNeedCaptchaOperate.checkNeedCaptchaOperate(keys, data);
        CheckNeedCaptchaDataVo checkNeedCaptchaDataVo = new CheckNeedCaptchaDataVo();
        checkNeedCaptchaDataVo.setCaptchaId(id);
        checkNeedCaptchaDataVo.setVerifyCaptcha(result);
        return checkNeedCaptchaDataVo;
    }

    @Override
    public ResponseModel getCaptcha(CaptchaVO captchaVO) {
        return captchaHandle.getCaptcha(captchaVO);
    }

    @Override
    public ResponseModel verifyCaptcha(CaptchaVO captchaVO) {
        return captchaHandle.checkCaptcha(captchaVO);
    }
}
