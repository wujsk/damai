package com.damai.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.damai.data.BaseEntity;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: haonan
 * @description: 用户邮箱 实体
 */
@Data
@TableName("d_user_email")
public class UserEmail extends BaseEntity implements Serializable {

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
     * 邮箱
     */
    private String email;
}
