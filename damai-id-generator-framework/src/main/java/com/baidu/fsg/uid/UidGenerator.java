package com.baidu.fsg.uid;


import com.baidu.fsg.uid.exception.UidGenerateException;

/**
 * @author: haonan
 * @description: id生成器
 */
public interface UidGenerator {

    /**
     * 生成id
     * @return
     * @throws UidGenerateException
     */
    long getUid() throws UidGenerateException;

    /**
     * 生成id
     * @return
     * @throws UidGenerateException
     */
    long getId();

    /**
     * 获取订单编号
     * @param userId 用户id
     * @param tableCount 分表数量
     * @return 结果
     * */
    long getOrderNumber(long userId,long tableCount);

    /**
     * Parse the UID into elements which are used to generate the UID. <br>
     * Such as timestamp & workerId & sequence...
     *
     * @param uid
     * @return Parsed info
     */
    String parseUid(long uid);
}
