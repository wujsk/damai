package com.damai.dto;


import lombok.Data;

import java.util.Date;

/**
 * @author: haonan
 * @description: elasticsearch查询参数
 */
@Data
public class EsDataQueryDto {

    /**
     * 参数名称
     */
    private String paramName;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 如果是时间类型，则不使用paramValue 使用startTime和endTime
     * */
    private Date startTime;

    /**
     * 如果是时间类型，则不使用paramValue 使用startTime和endTime
     * */
    private Date endTime;

    /**
     * 是否分词查询(默认不分词)
     * */
    private boolean analyse = false;
}
