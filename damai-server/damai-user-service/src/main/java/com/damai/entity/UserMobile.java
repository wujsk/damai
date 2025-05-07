package com.damai.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.damai.data.BaseEntity;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: haonan
 * @description: 用户手机号 实体
 */
@Data
@TableName("d_user_mobile")
public class UserMobile extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 手机号
     */
    private String mobile;
}
